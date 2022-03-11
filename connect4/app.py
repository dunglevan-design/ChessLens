#!/usr/bin/env python

import asyncio
import itertools
import secrets

import websockets
import json
from connect4 import PLAYER1, PLAYER2
from connect4 import Connect4

JOIN = {}
WATCH = {}

async def error(websocket, message):
    event = {
        "type":"error",
        "message": message
    }
    await websocket.send(json.dumps(event))

async def play(game, player, connected, column):
    row = game.play(player, column)
    for websocket in connected:
        event = {
            "type" : "play",
            "player": player,
            "column": column,
            "row": row,
        }
        await websocket.send(json.dumps(event))

        if game.winner is not None:
            event = {
                "type": "win",
                "player": game.winner
            }
            await websocket.send(json.dumps(event))


async def start(websocket):
    game = Connect4()
    turns = itertools.cycle([PLAYER1, PLAYER2])
    connected = {websocket}

    join_key = secrets.token_urlsafe(12)
    watch_key = secrets.token_urlsafe(12)
    JOIN[join_key] = game, connected
    WATCH[watch_key] = JOIN[join_key]


    try:
        event = {
            "type": "init",
            "join": join_key,
            "watch": watch_key
        }
        await websocket.send(json.dumps(event))
        print("first player started the game", id(game))
        async for message in websocket:
            event = json.loads(message)
            assert event["type"] == "play"
            column = event["column"]
            await play(game, PLAYER1, connected, column)
    finally:
        del JOIN[join_key]


async def join(websocket, join_key):
    try:
        game, connected = JOIN[join_key]
    except KeyError:
        await error(websocket, "Game not found")
        return

    connected.add(websocket)
    try:
        print("second player join the game", id(game))
        async for message in websocket:
            event = json.loads(message)
            assert event["type"] == "play"
            column = event["column"]
            await play(game, PLAYER2, connected, column)
 
    finally:
        connected.remove(websocket)

async def watch(websocket, watch_key):
    try:
        game, connected = WATCH[watch_key]
    except KeyError:
        await error(websocket, "Game not found")
        return
    connected.add(websocket)
    try:
        print("Another player is watching the game", id(game))
        async for message in websocket:
            print("Still watching")
    finally:
        connected.remove(websocket)
    
    

async def handler(websocket):
    message = await websocket.recv()
    event = json.loads(message)
    assert event["type"] == "init"

    if "join" in event:
        await join(websocket, event["join"])
    elif "watch" in event:
        await watch(websocket, event["watch"])
    else:
        await start(websocket)




async def main():
    async with websockets.serve(handler, "", 8001):
        await asyncio.Future()


if __name__ == "__main__":
    asyncio.run(main())
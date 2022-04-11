import asyncio
from operator import truediv
import string
from threading import Thread
import websockets
import json
import berserk
from requests_oauthlib import OAuth2Session
import os

from Game import Game
from models import game
os.environ['OAUTHLIB_INSECURE_TRANSPORT'] = '1'


async def consumer(action, websocket, client : berserk.clients.Client , board : berserk.clients.Board):
    #do stuff base on action type 
    type = action["type"]
    if type == "challengeDirectly":
        data = action["data"]
        challenge_data =client.challenges.create(data["username"], rated=False, color=berserk.enums.Color.WHITE, variant= berserk.enums.Variant.STANDARD)
        print(challenge_data)

    elif type == "move":
        print(action)
        data = action["data"]
        move = data["move"]
        gameid = data["game"]
        board.make_move(gameid, move)
        

    elif type == "test":
        print("action: ", action)


async def consumer_handler(websocket, client, board):
    async for message in websocket:
        action = json.loads(message)
        await consumer(action, websocket, client, board)

#new thread, create new event loop to run producer.
def producer_thread(websocket, client, board):
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)

    loop.run_until_complete(producer(websocket, client, board))
    loop.close()

async def producer(websocket, client:berserk.clients.Client, board:berserk.clients.Board):
    for event in board.stream_incoming_events():
        print(event)
        if event["type"] == "gameStart":
            print("opponent accepted the challenge")
            isWhite = True
            action = {
                "type": "gameStart",
                "data": {
                    "color": "white",
                    "isWhite": True,
                    "time" : "",
                    "gameid": event["game"]["id"]
                }
            }
            await websocket.send(json.dumps(action))
            game = Game(board, event['game']['id'], isWhite, websocket )
            game.start()

        


async def producer_handler(websocket, client:berserk.clients.Client, board:berserk.clients.Board):
    # listen to shit on new thread so it doesnt block
    t1 = Thread(target=producer_thread, args=(websocket, client, board))
    t1.daemon = True # so ending the main thread will end this thread
    t1.start()
    # t1.join() is dubious because it blocks this thread and cant switch to other coroutine.
    # run forever, if this returns, all tasks are terminated
    await asyncio.Future()




    
async def start(websocket, token:string):
    # create client session to lichess API
    session = OAuth2Session("jsapp6", token={"access_token": token})
    client = berserk.clients.Client(session)
    board = berserk.clients.Board(session)

    # init game model


    consumer_task = asyncio.create_task(consumer_handler(websocket, client, board))
    producer_task = asyncio.create_task(producer_handler(websocket, client, board))

    done, pending = await asyncio.wait([consumer_task, producer_task], return_when=asyncio.FIRST_COMPLETED)
    print("task as done")

    #for testing
    for task in done:
        print(task, "done")
    #terminatin pending task
    for task in pending:
        print(task, "terminating")
        task.cancel()

async def handler(websocket):    
    #Connection open, user not signed in
    message = await websocket.recv()
    action = json.loads(message)
    assert action["type"] == "init"
    print(action["message"])

    # user signed in.
    message = await websocket.recv()
    action = json.loads(message)
    assert action["type"] == "signin"
    token = action["data"]["token"]
    print(token)
    await start(websocket, token)


async def main():
    
    async with websockets.serve(handler, "", 8001):
        await asyncio.Future() #run forever

if __name__ == "__main__":
    asyncio.run(main())
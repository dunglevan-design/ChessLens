import asyncio
import string
from threading import Thread
import websockets
import json
import berserk
from requests_oauthlib import OAuth2Session
import os
os.environ['OAUTHLIB_INSECURE_TRANSPORT'] = '1'


async def consumer(action, websocket, client : berserk.clients.Client , board : berserk.clients.Board):
    #do stuff base on action type 
    print("14 works")
    type = action["type"]
    if type == "challenge_directly":
        data = action["data"]
        challenge_data =client.challenges.create(data["username"], rated=False, color=berserk.enums.Color.WHITE, variant= berserk.enums.Variant.STANDARD)
        print(challenge_data)
    elif type == "test":
        print("action: ", action)


async def consumer_handler(websocket, client, board):
    print("25 works")
    async for message in websocket:
        print("26 works")
        action = json.loads(message)
        await consumer(action, websocket, client, board)

def producer(websocket, client:berserk.clients.Client, board:berserk.clients.Board):
    for event in board.stream_incoming_events():
        print("event: ", event)
        


async def producer_handler(websocket, client:berserk.clients.Client, board:berserk.clients.Board):
    print("producer handler")
    # listen to shit on new thread so it doesnt block
    t1 = Thread(target=producer, args=(websocket, client, board))
    t1.daemon = True # so ending the main thread will end this thread
    t1.start()
    # t1.join() is dubious because it blocks this thread and cant switch to other coroutine.
    # run forever, if this returns, all tasks are terminated
    await asyncio.Future()




    
async def start(websocket:websockets, token:string):
    # create client session to lichess API
    session = OAuth2Session("nativeappreact", token={"access_token": "lio_Fw5iaQKZt1tT2x0Xgvo8dCta4JnrfGf8"})
    client = berserk.clients.Client(session)
    board = berserk.clients.Board(session)

    # init game model
    #threading to read incoming events

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
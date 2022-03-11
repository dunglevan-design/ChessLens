import asyncio
import websockets
import json
import berserk
from requests_oauthlib import OAuth2Session
import os
os.environ['OAUTHLIB_INSECURE_TRANSPORT'] = '1'


async def consumer(message,websocket):
    #do stuff base on message type 
    data = json.loads(message)
    print(data)
    event = {
        "type": "test",
        "message": "this is a test"
    }
    await websocket.send(json.dumps(event))

    
async def start(websocket):
    # create client session to lichess API
    session = OAuth2Session("nativeappreact", token={"access_token": "lio_Fw5iaQKZt1tT2x0Xgvo8dCta4JnrfGf8"})
    client = berserk.Client(session)
    



    # init game model
    async for message in websocket:
        event = json.loads(message)
        print(event)


async def handler(websocket):
    message = await websocket.recv()
    event = json.loads(message)
    assert event["type"] == "init"
    # TODO: GET ACCESS TOKEN. Pass to start

    await start(websocket)


async def main():
    
    async with websockets.serve(handler, "", 8001):
        await asyncio.Future() #run forever

if __name__ == "__main__":
    asyncio.run(main())
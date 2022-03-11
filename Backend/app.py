import asyncio
import websockets
import json
import berserk
from requests_oauthlib import OAuth2Session
import os
os.environ['OAUTHLIB_INSECURE_TRANSPORT'] = '1'


async def consumer(action, websocket, client : berserk.clients.Client , board : berserk.clients.Board):
    #do stuff base on action type 
    type = action["type"]
    if type == "challenge_directly":
        data = action["data"]
        challenge_data =client.challenges.create(data["username"], rated=False, color=berserk.enums.Color.WHITE, variant= berserk.enums.Variant.STANDARD)
        print(challenge_data)



    
async def start(websocket, token):
    # create client session to lichess API
    session = OAuth2Session("nativeappreact", token={"access_token": "lio_Fw5iaQKZt1tT2x0Xgvo8dCta4JnrfGf8"})
    client = berserk.clients.Client(session)
    board = berserk.clients.Board(session)

    # init game model
    #threading to read incoming events



    async for message in websocket:
        action = json.loads(message)
        await consumer(action,websocket, client, board)


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
import asyncio
import websockets
import json


async def consumer(message,websocket):
    #do stuff base on message type 
    data = json.loads(message)
    print(data)
    event = {
        "type": "test",
        "message": "this is a test"
    }
    await websocket.send(json.dumps(event))

    


async def handler(websocket):
    # create client session to lichess API
    # init game model
    async for message in websocket:
        await consumer(message,websocket)


async def main():
    async with websockets.serve(handler, "", 8001):
        await asyncio.Future() #run forever

if __name__ == "__main__":
    asyncio.run(main())
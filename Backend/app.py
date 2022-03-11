import asyncio
import websockets
import json



async def handler(websocket):
    


async def main():
    async with websockets.serve(handler, "", 8081):
        await asyncio.Future() #run forever

if __name__ == "__main__":
    asyncio.run(main())
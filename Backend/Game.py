import asyncio
import json
import threading
import berserk
import websockets

class Game(threading.Thread):
    def __init__(self, board:berserk.clients.Board, game_id, isWhite, websocket, **kwargs):
        super().__init__(**kwargs)
        self.game_id = game_id
        self.board = board
        self.isWhite = isWhite
        self.websocket = websocket
        self.stream = board.stream_game_state(game_id)
        #self.loop = asyncio.new_event_loop()
        self.running = False


    def run(self) -> None:
        print("game started")
        for event in self.stream:
            if event['type'] == 'gameState':
                self.handle_state_change(event)
    
    def handle_state_change(self, game_state):
        turn = len(game_state["moves"].split())-1
        # if white's just moved
        # We are always white.
        if turn%2 == 0:
            pass
        else:
            opponentmove = game_state["moves"].split()[-1]
            action = {
                "type": "move",
                "data": {
                    "move": opponentmove
                }
            }
            loop = asyncio.new_event_loop()
            asyncio.set_event_loop(loop)
            loop.run_until_complete(self.send_socket_message(action, self.websocket))
            loop.close()
            
            #asyncio.run_coroutine_threadsafe(self.send_socket_message(action, self.websocket), self.loop)
            
    async def send_socket_message(self, action, websocket):
        await websocket.send(json.dumps(action))
            
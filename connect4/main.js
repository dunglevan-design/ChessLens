import { createBoard, playMove } from "./connect4.js";
const websocket = new WebSocket("ws://localhost:8001/");
window.addEventListener("DOMContentLoaded", () => {
  // Initialize the UI.
  const board = document.querySelector(".board");
  createBoard(board);

  const websocket = new WebSocket("ws://localhost:8001");
  initGame(websocket);
  receiveMoves(board, websocket);
  sendMoves(board, websocket);
});

function showMessage(message) {
  window.setTimeout(() => window.alert(message), 50);
}
function sendMoves(board, websocket) {
  board.addEventListener("click", ({ target }) => {
    const column = target.dataset.column;
    if (column === undefined) {
      return;
    }
    const event = {
      type: "play",
      column: parseInt(column, 10),
    };
    websocket.send(JSON.stringify(event));
  });
}

function receiveMoves(board, websocket) {
  websocket.addEventListener("message", ({ data }) => {
    const event = JSON.parse(data);
    switch (event.type) {
      case "init":
          document.querySelector(".join").href = "?join=" + event.join;
          document.querySelector(".watch").href = "?watch=" + event.watch;
          break;
      case "play":
        playMove(board, event.player, event.column, event.row);
        break;
      case "win":
        console.log("win")
        showMessage(`Player ${event.player} wins`);
        websocket.close(1000);
      case "error":
        showMessage(event.message);
        break;
      default:
        throw new Error("Unsupported event type");
    }
  });
}

function initGame(websocket){
    websocket.addEventListener("open", () => {
        //send an "init" event according to who is connecting
        const params = new URLSearchParams(window.location.search);
        let event = {type: "init"};
        if(params.has("join")){
            //2nd player join the game
            event.join = params.get("join")
        }
        else if (params.has("watch")){
            event.watch = params.get("watch")
        }
        else {
            //first player start a new game
        }
        websocket.send(JSON.stringify(event))
    })
}

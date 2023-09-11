const net = require("net");
const readline = require("readline");

const SERVER_ADDRESS = "localhost";
const SERVER_PORT = 5000;

const sockets = new net.Socket();

const username = readline();

const readlinePrompt = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

sockets.connect(SERVER_PORT, SERVER_ADDRESS, () => {
  console.log("connected to chat");
  readlinePrompt.prompt();
});

sockets.on("data", (data) => {
  console.log(data.toString());
  readlinePrompt.prompt();
});

readlinePrompt.on("line", (input) => {
  if (input.includes("quit") && input.length === "quit".length)
    return sockets.destroy();

  sockets.write(`(${username})` username + input + "\n");
  readlinePrompt.prompt();
});

sockets.on("close", () => {
  readlinePrompt.close();
  console.log("Connection closed");
});

sockets.on("error", (err) => {
  console.error("Error:", err.message);
  readlinePrompt.close();
});

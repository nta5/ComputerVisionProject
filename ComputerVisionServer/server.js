const express = require('express');
const app = express();
const server = require('http').createServer(app);
const WebSocket = require('ws');

const wss = new WebSocket.Server({server: server});

wss.on("connection", function(ws) {
    console.log("A new client connected!");
    
     // runs whenever the server receieves a message from any client
    ws.on("message", message => {
        console.log("recieved message from client");

        // broadcasts the message back to each client
        wss.clients.forEach(client => {
            if (client.readyState == WebSocket.OPEN) {
                console.log("sending message to client: " + message);
                client.send(message.toString());
            }
        });

    });

});

app.get("/", (req, res) => {
    res.send("Hello World!");
});

const port = 8080;
server.listen(port, () => {
    console.log("Listing on port " + port + "!");
});
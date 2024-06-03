window.onload = () => connect();
window.onclose = () => disconnect();

const stompClient = new StompJs.Client({
  brokerURL: 'ws://localhost:8080/chat'
});

stompClient.onConnect = (frame) => {
  console.log('Connected: ' + frame);
  stompClient.subscribe('/chat/messages', (messageJson) => {
    const message = JSON.parse(messageJson.body);
    showMessage(message.sender, message.time, message.content);
  });
};

stompClient.onWebSocketError = (error) => {
  console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
  console.error('Broker reported error: ' + frame.headers['message']);
  console.error('Additional details: ' + frame.body);
};

function connect() {
  stompClient.activate();
}

function disconnect() {
  stompClient.deactivate();
  console.log("Disconnected");
}

function sendMessage(event, username) {
  event.preventDefault();
  stompClient.publish({
    destination: "/app/chat",
    body: JSON.stringify({
      'sender': username,
      'content': document.getElementById('message').value
    })
  });
  document.getElementById('message').value = '';
  console.log(username);
  return false;
}

const showMessage = (sender, time, message) => {
  const senderMessage = `<div class="alert alert-primary" role="alert">[${time}] ${sender}: ${message}</div>`;
  document.getElementById('messages').innerHTML += senderMessage
}

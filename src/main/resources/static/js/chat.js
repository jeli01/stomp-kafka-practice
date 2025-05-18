let stompClient = null;
let currentRoomId = null;
let username = null;

// DOM 요소
const connectButton = document.getElementById('connect-button');
const messageForm = document.getElementById('message-form');
const messageInput = document.getElementById('message');
const messagesContainer = document.getElementById('chat-messages');
const roomList = document.getElementById('room-list');
const usernameInput = document.getElementById('username');
const usernamePage = document.getElementById('username-page');
const chatPage = document.getElementById('chat-page');
const createRoomButton = document.getElementById('create-room-button');
const createRoomSubmit = document.getElementById('create-room-submit');
const currentRoomName = document.getElementById('current-room-name');

// 부트스트랩 모달 
const createRoomModal = new bootstrap.Modal(document.getElementById('createRoomModal'));

// 이벤트 리스너 등록
connectButton.addEventListener('click', connect);
messageForm.addEventListener('submit', sendMessage);
createRoomButton.addEventListener('click', showCreateRoomModal);
createRoomSubmit.addEventListener('click', createChatRoom);

// WebSocket 연결
function connect() {
    username = usernameInput.value.trim();
    
    if (!username) {
        alert('사용자 이름을 입력하세요.');
        return;
    }
    
    // 화면 전환
    usernamePage.style.display = 'none';
    chatPage.style.display = 'block';
    
    // 채팅방 목록 불러오기
    fetchChatRooms();
    
    // WebSocket 연결
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    
    stompClient.connect({}, onConnected, onError);
}

// 연결 성공 시 콜백
function onConnected() {
    console.log('WebSocket 연결 성공');
}

// 연결 실패 시 콜백
function onError(error) {
    console.error('WebSocket 연결 오류:', error);
    alert('서버 연결에 실패했습니다. 페이지를 새로고침 해주세요.');
}

// 채팅방 구독
function subscribeToChatRoom(roomId, roomName) {
    // 이전 구독이 있으면 해제
    if (currentRoomId) {
        stompClient.unsubscribe('sub-' + currentRoomId);
    }
    
    // 새 채팅방 구독
    currentRoomId = roomId;
    currentRoomName.textContent = roomName;
    
    // 입력창 활성화
    messageInput.disabled = false;
    messageForm.querySelector('button').disabled = false;
    
    // 이전 메시지 지우기
    messagesContainer.innerHTML = '';
    
    // 이전 메시지 이력 가져오기
    fetchChatHistory(roomId);
    
    // 채팅방 구독
    stompClient.subscribe('/topic/chat/' + roomId, onMessageReceived, { id: 'sub-' + roomId });
    
    // 사용자 입장 메시지 전송
    stompClient.send("/app/chat.addUser", {}, JSON.stringify({
        sender: username,
        type: 'JOIN',
        roomId: roomId
    }));
    
    // 활성 채팅방 표시
    document.querySelectorAll('.room-item').forEach(item => {
        item.classList.remove('active');
        if (item.dataset.roomId === roomId) {
            item.classList.add('active');
        }
    });
}

// 메시지 전송
function sendMessage(event) {
    event.preventDefault();
    
    const messageContent = messageInput.value.trim();
    
    if (!messageContent || !currentRoomId) {
        return;
    }
    
    const chatMessage = {
        sender: username,
        content: messageContent,
        type: 'CHAT',
        roomId: currentRoomId
    };
    
    stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(chatMessage));
    messageInput.value = '';
}

// 메시지 수신
function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    displayMessage(message);
}

// 메시지 화면에 표시
function displayMessage(message) {
    const messageElement = document.createElement('div');
    
    if (message.type === 'JOIN') {
        messageElement.classList.add('message', 'system');
        message.content = message.sender + '님이 입장했습니다.';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('message', 'system');
        message.content = message.sender + '님이 퇴장했습니다.';
    } else {
        // 보낸 사람이 자신인지 확인
        if (message.sender === username) {
            messageElement.classList.add('message', 'sent');
        } else {
            messageElement.classList.add('message', 'received');
        }
        
        // 보낸 사람 이름 표시
        const senderElement = document.createElement('div');
        senderElement.classList.add('sender');
        senderElement.textContent = message.sender;
        messageElement.appendChild(senderElement);
    }
    
    // 메시지 내용
    const contentElement = document.createElement('div');
    contentElement.textContent = message.content;
    messageElement.appendChild(contentElement);
    
    // 시간 표시
    if (message.timestamp) {
        const timestampElement = document.createElement('div');
        timestampElement.classList.add('timestamp');
        
        // ISO 날짜 형식을 가독성 좋게 변환
        const date = new Date(message.timestamp);
        timestampElement.textContent = date.toLocaleTimeString();
        
        messageElement.appendChild(timestampElement);
    }
    
    messagesContainer.appendChild(messageElement);
    
    // 스크롤을 맨 아래로 이동
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

// 채팅방 목록 조회
async function fetchChatRooms() {
    try {
        const response = await axios.get('/api/chat/rooms');
        displayChatRooms(response.data);
    } catch (error) {
        console.error('채팅방 목록 조회 실패:', error);
    }
}

// 채팅방 목록 화면에 표시
function displayChatRooms(rooms) {
    roomList.innerHTML = '';
    
    if (rooms.length === 0) {
        roomList.innerHTML = '<div class="text-center text-muted">채팅방이 없습니다.</div>';
        return;
    }
    
    rooms.forEach(room => {
        const roomElement = document.createElement('div');
        roomElement.classList.add('room-item', 'mb-2');
        roomElement.dataset.roomId = room.roomId;
        roomElement.textContent = room.name;
        
        roomElement.addEventListener('click', () => {
            subscribeToChatRoom(room.roomId, room.name);
        });
        
        roomList.appendChild(roomElement);
    });
}

// 채팅방 생성 모달 표시
function showCreateRoomModal() {
    document.getElementById('room-name').value = '';
    createRoomModal.show();
}

// 새 채팅방 생성
async function createChatRoom() {
    const roomName = document.getElementById('room-name').value.trim();
    
    if (!roomName) {
        alert('채팅방 이름을 입력하세요.');
        return;
    }
    
    try {
        const response = await axios.post('/api/chat/rooms', { name: roomName });
        createRoomModal.hide();
        
        // 채팅방 목록 갱신
        fetchChatRooms().then(() => {
            // 새로 생성된 채팅방 바로 구독
            subscribeToChatRoom(response.data.roomId, response.data.name);
        });
    } catch (error) {
        console.error('채팅방 생성 실패:', error);
        alert('채팅방 생성에 실패했습니다.');
    }
}

// 채팅 메시지 이력 조회
async function fetchChatHistory(roomId) {
    try {
        const response = await axios.get(`/api/chat/rooms/${roomId}/messages`);
        
        // 메시지 이력 표시
        response.data.forEach(message => {
            displayMessage(message);
        });
    } catch (error) {
        console.error('메시지 이력 조회 실패:', error);
    }
}

// 브라우저 창 닫기 전에 연결 해제
window.addEventListener('beforeunload', () => {
    if (stompClient && currentRoomId) {
        // 퇴장 메시지 전송
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify({
            sender: username,
            type: 'LEAVE',
            roomId: currentRoomId
        }));
        
        // 연결 해제
        stompClient.disconnect();
    }
});

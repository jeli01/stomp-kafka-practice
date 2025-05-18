


https://github.com/user-attachments/assets/537cf316-5e4a-4e26-a272-2418509076d3


# STOMP-Kafka 채팅 애플리케이션

이 프로젝트는 Spring Boot, STOMP(Simple Text Oriented Messaging Protocol)와 Apache Kafka를 사용하여 구현된 실시간 채팅 애플리케이션입니다. 

사용자들이 채팅방을 생성하고 참여하여 실시간으로 메시지를 주고받을 수 있는 기능을 제공합니다.

stomp 및 kafka를 공부하기 위한 프로젝트로 로컬에서 실행됩니다

## 기술 스택

- **백엔드**: Spring Boot 3.4.5, Spring WebSocket, Spring Kafka, Spring Data JPA
- **메시지 브로커**: Apache Kafka 4.0 (KRaft 모드)
- **데이터베이스**: MySQL 8.0
- **프론트엔드**: HTML, CSS, JavaScript, Bootstrap 5, SockJS, STOMP.js

## 주요 기능

- 실시간 채팅 메시지 송수신
- 채팅방 생성 및 관리
- 사용자 입장/퇴장 알림
- 채팅 메시지 영구 저장
- 채팅 이력 조회

## 설치 및 실행 방법

### 1. 사전 요구사항

- JDK 21
- MySQL 8.0
- Apache Kafka 4.0

### 2. MySQL 설정

1. MySQL 서버를 실행하고 `kafka_chat` 데이터베이스를 생성합니다:

```sql
CREATE DATABASE kafka_chat;
```

2. 필요한 경우 `application.properties` 파일의 데이터베이스 설정을 수정합니다:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/database_name
spring.datasource.username=username
spring.datasource.password=password
```

### 3. Kafka 설정 (KRaft 모드)

#### 3.1. Kafka 다운로드 및 설치

1. [Apache Kafka 웹사이트](https://kafka.apache.org/downloads)에서 Kafka 4.0.0 다운로드
2. 다운로드한 파일 압축 해제:
   ```bash
   tar -xzf kafka_2.13-4.0.0.tgz
   cd kafka_2.13-4.0.0
   ```

#### 3.2. KRaft 모드 설정

1. KRaft 설정 파일 생성:
   ```bash
   cat > config/kraft-server.properties << EOF
   # 서버 기본 설정
   process.roles=broker,controller
   node.id=1

   # 컨트롤러 설정
   controller.quorum.voters=1@localhost:9093

   # 리스너 설정
   listeners=PLAINTEXT://:9092,CONTROLLER://:9093
   inter.broker.listener.name=PLAINTEXT
   advertised.listeners=PLAINTEXT://localhost:9092
   controller.listener.names=CONTROLLER

   # 로그 디렉토리
   log.dirs=/tmp/kraft-combined-logs

   # 토픽 설정
   num.partitions=1
   default.replication.factor=1
   offsets.topic.replication.factor=1
   transaction.state.log.replication.factor=1
   transaction.state.log.min.isr=1
   EOF
   ```

2. 클러스터 ID 생성 및 스토리지 포맷:
   ```bash
   KAFKA_CLUSTER_ID=$(bin/kafka-storage.sh random-uuid)
   bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c config/kraft-server.properties
   ```

3. Kafka 서버 시작:
   ```bash
   bin/kafka-server-start.sh config/kraft-server.properties
   ```

4. 'chat-messages' 토픽 생성 (새 터미널에서):
   ```bash
   bin/kafka-topics.sh --create --topic chat-messages --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
   ```

5. 토픽 확인:
   ```bash
   bin/kafka-topics.sh --list --bootstrap-server localhost:9092
   ```


4. 웹 브라우저에서 애플리케이션 접속:
   ```
   http://localhost:8080
   ```

## 사용 방법

1. 웹 브라우저에서 애플리케이션에 접속합니다.
2. 사용자 이름을 입력하고 "연결" 버튼을 클릭합니다.
3. "새 채팅방" 버튼을 클릭하여 채팅방을 생성하거나, 기존 채팅방 목록에서 채팅방을 선택합니다.
4. 텍스트 입력창에 메시지를 입력하고 전송합니다.
5. 입장/퇴장 시 자동으로 알림 메시지가 표시됩니다.

## 프로젝트 구조

```
src/main/java/io/github/jeli01/stompkafkapractice/
├── config/
│   ├── KafkaConfig.java        # Kafka 설정
│   └── WebSocketConfig.java    # WebSocket 및 STOMP 설정
├── controller/
│   ├── ChatController.java     # 채팅 메시지 WebSocket 컨트롤러
│   └── ChatRoomController.java # 채팅방 REST API 컨트롤러
├── entity/
│   ├── ChatMessage.java        # 채팅 메시지 엔티티
│   ├── ChatMessageDto.java     # 채팅 메시지 DTO
│   ├── ChatRoom.java           # 채팅방 엔티티
│   └── ChatRoomDto.java        # 채팅방 DTO
├── repository/
│   ├── ChatMessageRepository.java # 채팅 메시지 레포지토리
│   └── ChatRoomRepository.java    # 채팅방 레포지토리
├── service/
│   └── ChatService.java        # 채팅 서비스 (Kafka 프로듀서/컨슈머)
└── StompKafkaPracticeApplication.java # 메인 애플리케이션 클래스
```

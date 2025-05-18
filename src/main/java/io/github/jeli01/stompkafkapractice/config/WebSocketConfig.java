package io.github.jeli01.stompkafkapractice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 웹소켓 서버에 연결하는 데 사용할 엔드포인트 등록
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // 개발 환경에서만 사용, 실제 환경에서는 특정 도메인만 허용
                .withSockJS();  // SockJS 지원 활성화
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 브로커 설정
        registry.enableSimpleBroker("/topic", "/queue");  // 구독 접두사 설정
        registry.setApplicationDestinationPrefixes("/app");  // 메시지 전송 접두사 설정
    }
}

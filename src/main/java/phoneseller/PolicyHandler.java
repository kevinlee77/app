package phoneseller;

import phoneseller.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @Autowired
    OrderRepository orderRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverShipped_OrderStatus(@Payload Shipped shipped){
        // 배송이 시작될 때 오더상태 변경

        if(shipped.isMe()){
            Optional<Order> orderOptional= orderRepository.findById(shipped.getOrderId());
            Order order = orderOptional.get();
            order.setStatus("app_policy_shipped 배송시작");
            orderRepository.save(order);

            System.out.println("##### listener OrderStatus : " + shipped.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPayCancelled_OrderStatus(@Payload PayCancelled payCancelled){
        // 결제가 취소될 때 오더상태 변경

        if(payCancelled.isMe()){
            Optional<Order> orderOptional= orderRepository.findById(payCancelled.getOrderId());
            Order order = orderOptional.get();
            order.setStatus("app_policy_paycancelled 결제취소");
            orderRepository.save(order);

            System.out.println("##### listener OrderStatus : " + payCancelled.toJson());
        }
    }

}

package phoneseller;

import javax.persistence.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.MimeTypeUtils;
import phoneseller.config.kafka.KafkaProcessor;
import phoneseller.external.Payment;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String item;
    private Integer qty;
    private String status;
    private String store;
    private Double price;

    @PostPersist
    public void onPostPersist(){
        System.out.println("******************** Order ******************* ");
        setStatus("Ordered");

        Ordered ordered = new Ordered();
        ordered.setId(this.getId());
        ordered.setItem(this.getItem());
        ordered.setQty(this.getQty());
        ordered.setStatus(this.getStatus());
        BeanUtils.copyProperties(this, ordered);
        ordered.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        phoneseller.external.Payment payment = new phoneseller.external.Payment();
        payment.setOrderId(this.getId());
        payment.setProcess("Ordered");

        // mappings goes here
         AppApplication.applicationContext.getBean(phoneseller.external.PaymentService.class)
            .pay(payment);

    }

    @PostUpdate
    public void onPostUpdate(){
//        System.out.println("*#*#*#*#*# order update *#*#*#*#*#*#*#*#");
//        PayCompleted payCompleted = new PayCompleted();
//        BeanUtils.copyProperties(this, payCompleted);
//        payCompleted.publish();

    }

    @PostRemove
    public void onPostRemove(){
        this.setStatus("OrderCancelled");
        OrderCancelled orderCancelled = new OrderCancelled();
        BeanUtils.copyProperties(this, orderCancelled);
        orderCancelled.publishAfterCommit();

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", item='" + item + '\'' +
                ", qty=" + qty +
                ", status='" + status + '\'' +
                ", store='" + store + '\'' +
                ", price=" + price +
                '}';
    }
}

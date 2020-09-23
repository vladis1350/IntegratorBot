package by.uniqo.bot.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "bot_responses")
public class BotResponse implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_botMessage")
    private long id;

    @Column(name = "tag_message")
    private String tag;

    @Column(name = "bot_message")
    private String botMessage;
}

package com.quizard.question.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "element_rankingu")
@DiscriminatorValue("ELEMENT_RANKINGU")
@Getter @Setter
@NoArgsConstructor
public class ElementRankingu extends Pytanie {
    // tresc z Pytanie = nazwa elementu do umieszczenia w tierliscie
    // brak oceniania — nie implementuje Ocenialne
}

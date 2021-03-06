package com.eunsil.guestbook.service;

import com.eunsil.guestbook.domain.dto.CardDTO;
import com.eunsil.guestbook.domain.entity.Card;
import com.eunsil.guestbook.domain.entity.User;
import com.eunsil.guestbook.repository.CardRepository;
import com.eunsil.guestbook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CardService {
    private CardRepository cardRepository;
    private UserRepository userRepository;

    @Autowired
    public CardService(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    public String insert(String name, String content) {
        User user = userRepository.findUserByName(name);

        Card card = Card.builder()
                .user(user)
                .content(content)
                .postDate(LocalDate.now())
                .build();
        cardRepository.saveAndFlush(card);
        return "ok";
    }

    public String update(String card_id, String content) {
        Card card = cardRepository.findById(card_id);
        if (card != null) {
            card.setContent(content);
            cardRepository.saveAndFlush(card);
            return "ok";
        } else {
            return "Not Existed Card";
        }
    }

    public String delete(String card_id) {
        Card card = cardRepository.findById(card_id);
        if (card != null) {
            cardRepository.deleteById(card.id);
            return "ok";
        } else {
            return "Not Existed Card";
        }
    }

    public List<CardDTO> search(String location, String option, String name, String content) {
        List<Card> cardList;

        if (location.equals("all")) { // ?????? ?????? ?????????
            if (option.equals("username")) { // ????????? ????????? ??????
                User user = userRepository.findUserByName(content);
                cardList = cardRepository.findAllByUserOrderByIdDesc(user);
            } else { // ???????????? ??????
                cardList = cardRepository.findAllByContent(content);
            }
        } else { // ??? ?????? ????????? - ???????????? ??????
            User user = userRepository.findUserByName(name);
            cardList = cardRepository.findAllByUserByContentOrderByIdDesc(content, user);
        }

        List<CardDTO> cardDTOList = new ArrayList<>();

        for (Card cards : cardList) {
            CardDTO card = CardDTO.builder()
                    .cardId(cards.getId())
                    .name(cards.getUser().getName())
                    .content(cards.content)
                    .postDate(cards.postDate)
                    .build();
            cardDTOList.add(card);
        }
        return cardDTOList;
    }

    public List<CardDTO> getAll(Integer page) {
        Pageable pageable = PageRequest.of(page, 5, Sort.Direction.DESC, "id");
        List<Card> cardList = cardRepository.findAllByOrderByIdDesc(pageable);
        List<CardDTO> cardDtoList = new ArrayList<>();

        for (Card cards : cardList) {
            CardDTO cardDto = CardDTO.builder()
                    .cardId(cards.getId())
                    .name(cards.getUser().getName())
                    .content(cards.getContent())
                    .postDate(cards.getPostDate())
                    .build();
            cardDtoList.add(cardDto);
        }
        return cardDtoList;
    }

    public CardDTO getDetail(String cardId) {
        Card card = cardRepository.findById(cardId);

        CardDTO cardDTO = CardDTO.builder()
                .cardId(card.getId())
                .name(card.user.getName())
                .content(card.getContent())
                .postDate(card.getPostDate())
                .build();
        return cardDTO;
    }

    public int getAllTotal() {
        return cardRepository.findAll().size();
    }

    public int getMyTotal(String username) {
        User user = userRepository.findUserByName(username);
        return cardRepository.findAllByUserOrderByIdDesc(user).size();
    }
}

package org.chelonix.jpscp.shared;

import lombok.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Article {

    public static abstract class Token {

        protected String text;

        Token(String text) {
            this.text = text;
        }

        public String text() {
            return text;
        }
    }

    public static class TextToken extends Token {

        public TextToken(String text) {
            super(text);
        }
    }

    public static class RubyToken extends Token {

        protected String reading;

        public RubyToken(String text, String reading) {
            super(text);
            this.reading = reading;
        }

        public String reading() {
            return reading;
        }
    }

    public static class Builder {
        private Article article;
        private Paragraph currentParagraph;
        private Sentence currentSentence;

        Builder() {
            this.article = new Article();
            this.currentParagraph = new Paragraph();
            this.currentSentence = new Sentence();
        }

        public void setTitle(String title) {
            article.setTitle(title);
        }

        public void add(Token token) {
            currentSentence.addToken(token);
        }

        public void newParagraph() {
            newSentence();
            if (!this.currentParagraph.isEmpty()) {
                this.article.addParagraph(currentParagraph);
            }
            this.currentParagraph = new Paragraph();
        }

        public void newSentence() {
            if (!this.currentSentence.isEmpty()) {
                this.currentParagraph.addSentence(currentSentence);
            }
            this.currentSentence = new Sentence();
        }

        public Article build() {
            if (!this.currentParagraph.isEmpty()) {
                this.article.addParagraph(currentParagraph);
            }
            return this.article;
        }
    }

    public static class Sentence implements Iterable<Token>{
        private List<Token> sentence = new ArrayList<>();

        void addToken(Token token) {
            this.sentence.add(token);
        }

        public Stream<Token> stream() {
            return this.sentence.stream();
        }

        @Override
        public Iterator<Token> iterator() {
            return this.sentence.iterator();
        }

        boolean isEmpty() {
            return this.sentence.isEmpty();
        }

        /**
         * Returns the sentence without the readings
         * @return the sentence as a string
         */
        public String rawString() {
            return stream().map(token -> token.text()).collect(Collectors.joining(""));
        }
    }

    public static class Paragraph implements Iterable<Sentence> {
        private List<Sentence> paragraph = new ArrayList<>();

        void addSentence(Sentence sentence) {
            this.paragraph.add(sentence);
        }

        public Stream<Sentence> stream() {
            return this.paragraph.stream();
        }

        @Override
        public Iterator<Sentence> iterator() {
            return this.paragraph.iterator();
        }

        boolean isEmpty() {
            return this.paragraph.isEmpty();
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Deque<Paragraph> paragraphs;

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private String title;

    Article() {
        this.paragraphs = new ArrayDeque<>();
    }

    void addParagraph(Paragraph paragraph) {
        this.paragraphs.add(paragraph);
    }
}

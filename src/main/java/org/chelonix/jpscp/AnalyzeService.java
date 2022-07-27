package org.chelonix.jpscp;

import org.chelonix.jpscp.shared.Article;
import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AnalyzeService {

    public String analyze(Article.Sentence sentence) {
        StringBuilder sb = new StringBuilder();
        Tokenizer tokenizer = new Tokenizer.Builder().build();
        for (Token token : tokenizer.tokenize(sentence.rawString())) {
            sb.append(token.getSurface() + "\t" + token.getAllFeatures()).append("\n");
        }
        return sb.toString();
    }
}

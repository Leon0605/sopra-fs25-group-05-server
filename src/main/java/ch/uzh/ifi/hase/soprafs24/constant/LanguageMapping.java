package ch.uzh.ifi.hase.soprafs24.constant;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;

@Embeddable
public class LanguageMapping {

    @ElementCollection
    @CollectionTable(name = "message_language_mapping", joinColumns = @JoinColumn(name = "message_id"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "text")
    private Map<String, String> languageMap;

    public LanguageMapping() {
        this.languageMap = new HashMap<>();
        languageMap.put("en", "");
        languageMap.put("de", "");
        languageMap.put("fr", "");
        languageMap.put("es", "");
        languageMap.put("it", "");
    }

    public String getContent(String languageKey) {
        return languageMap.get(languageKey);
    }

    public void setContent(String languageKey, String contentValue) {
        languageMap.put(languageKey, contentValue);
    }


    public Map<String, String> getLanguageMap() {
        return languageMap;
    }
    public void setLanguageMap(Map<String, String> languageMap) {
        this.languageMap = languageMap;
    }
}


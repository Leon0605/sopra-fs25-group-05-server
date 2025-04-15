package ch.uzh.ifi.hase.soprafs24.service.API;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.azure.ai.translation.text.TextTranslationClient;
import com.azure.ai.translation.text.TextTranslationClientBuilder;
import com.azure.ai.translation.text.models.InputTextItem;
import com.azure.ai.translation.text.models.TextType;
import com.azure.ai.translation.text.models.TranslatedTextItem;
import com.azure.core.credential.AzureKeyCredential;

@Service
public class AzureAPI{

    public static String AzureTranslate(String originalMessage,String originalLanguage, String targetLanguage){
        String apiKey = "c32df61d02b44bb98fdd3ab6456207fb";
        AzureKeyCredential credential = new AzureKeyCredential(apiKey);

        TextTranslationClient client = new TextTranslationClientBuilder()
        .endpoint("https://api.cognitive.microsofttranslator.com")
        .region("australiaeast")
        .credential(credential)
        .buildClient();

        String from = originalLanguage;
        List<String> targetLanguages = new ArrayList<>();
        targetLanguages.add(targetLanguage);
        List<InputTextItem> content = new ArrayList<>();
        content.add(new InputTextItem(originalMessage));

        List<TranslatedTextItem> translations = client.translate(targetLanguages, content, null, from, TextType.PLAIN, null, null, null, false, false, null, null, null, false);

        return translations.get(0).getTranslations().get(0).getText();
    }
}
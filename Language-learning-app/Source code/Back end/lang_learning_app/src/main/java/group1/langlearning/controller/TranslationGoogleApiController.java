package group1.langlearning.controller;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import group1.langlearning.models.GeneralResponse;
import group1.langlearning.models.TranslationRequestModel;
import group1.langlearning.repositories.LanguageCodesRepository;
import group1.langlearning.utils.CommonTasks;
import group1.langlearning.entity.LanguageCodes;
import group1.langlearning.models.Constants;

@RequestMapping(path = "/translate")
@RestController
@CrossOrigin("*")
public class TranslationGoogleApiController {
    
    Gson gson = new GsonBuilder().create(); 

    @Autowired
    private CommonTasks commonTasks;

    @Autowired
    private LanguageCodesRepository languageCodesRepository;

    @GetMapping(value = "/languagecodes")
    public ResponseEntity<GeneralResponse> getLanguageCodes() {

        Iterable<LanguageCodes> codes = languageCodesRepository.findAll();
        Iterator itr = codes.iterator();
        List<String> languages = new ArrayList<>();
        while(itr.hasNext())
        {
            LanguageCodes code = (LanguageCodes) itr.next();
            languages.add(code.getCountry());
        }
        return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
        Constants.FETCH_LANGUAGE_CODES, Constants.FETCH_LANGUAGE_CODES_MSG, languages), HttpStatus.OK);
       
    }

    @PostMapping(value = "/source/target")
    public ResponseEntity<GeneralResponse> translate(@RequestBody String data) {
   
    String translatedText = null;    

    TranslationRequestModel model = gson.fromJson(data,TranslationRequestModel.class);
    if(data!=null)
        {
            if(!commonTasks.checkString(model.getSourceLanguage()) || !commonTasks.checkString(model.getTargetLanguage())
            || !commonTasks.checkString(model.getInputText()))
            {
           return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
           Constants.USERNAME_NULL, Constants.USERNAME_NULL_MSG, null), HttpStatus.OK);
            }
            try {
                // URL url = new URL("https://translate.googleapis.com/translate_a/single?client=gtx&sl=" 
                // + model.getSourceLanguage() + "&tl=" + model.getTargetLanguage() + "&dt=t&q=" 
                // + URLEncoder.encode(model.getInputText(), "UTF-8"));
        //         String inputText = "Hello Vyshnavi"; // The text to be translated
        // String inputLang = "en"; // The input language code
        // String outputLang = "te"; // The output language code
        LanguageCodes srcLangCode = languageCodesRepository.findByCountry(model.getSourceLanguage());
        LanguageCodes targetLangCode = languageCodesRepository.findByCountry(model.getTargetLanguage());
        if(srcLangCode!= null && targetLangCode!= null)
        {
            
        
        String inputText = model.getInputText(); // The text to be translated
        String inputLang = srcLangCode.getLanguage_code(); // The input language code
        String outputLang = targetLangCode.getLanguage_code(); // The output language code

                    URL url = new URL("https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + inputLang + "&tl=" + outputLang + "&dt=t&q=" + URLEncoder.encode(inputText, "UTF-8"));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),StandardCharsets.UTF_8));

                    String line;
                    StringBuffer response = new StringBuffer();
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
        
                    // Extract the translated text from the JSON response
                    translatedText = response.toString().split("\"")[1];
        
                    System.out.println("translatedText : "+translatedText);
        
                PrintWriter writer = new PrintWriter("Output.txt", "UTF-8"); // create a PrintWriter for the output file
                writer.println("translatedText : "+translatedText); // write a line to the output file
                writer.close(); // close the PrintWriter to flush any remaining data
        }
        else{
            return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
         Constants.REQUEST_DATA_MISSING, Constants.REQUEST_DATA_MISSING_MSG, null), HttpStatus.OK);
        
        }
            } catch (Exception e) {
                e.printStackTrace();
                
                return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
                Constants.EXCEPTION_WHILE_TRANSLATING, Constants.EXCEPTION_WHILE_TRANSLATING_MSG, null), HttpStatus.OK);

            }

            
       return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.TRUE,
       Constants.INPUT_TEXT_TRANSLATED, Constants.INPUT_TEXT_TRANSLATED_MSG, translatedText), HttpStatus.OK);

        }

        else
        {
         return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
         Constants.REQUEST_DATA_MISSING, Constants.REQUEST_DATA_MISSING_MSG, null), HttpStatus.OK);
        }
    }
}

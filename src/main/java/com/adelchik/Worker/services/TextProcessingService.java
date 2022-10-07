package com.adelchik.Worker.services;

import com.adelchik.Worker.db.entities.TextEntity;
import com.adelchik.Worker.db.repository.TextRepository;
import com.adelchik.Worker.exception.NoWordsInTheChunkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TextProcessingService {

    @Autowired
    private TextRepository repo;

    private String onePartFile;

    private String[] cleanOriginalText(String originalText) throws NoWordsInTheChunkException{

        String filteredText = deleteAllNonLettersFromTextWithExceptions(originalText);
        String[] dirtyWordArray = separateTextIntoWords(filteredText);
        String[] cleanWordArray = wordArrayToLowerCase(dirtyWordArray);


        return cleanWordArray;
    }

    private String insertWordsIntoMap(HashMap<String, Integer> givenMap, String[] cleanWordArray){

        HashMap<String, Integer> map = processWordsIntoWordAndFrequencyPair(cleanWordArray, givenMap);
        HashMap<String, Integer> sortedMap = orderWordsByFrequency(map);
        String readyString = convertMapToString(sortedMap);

        return readyString;
    }

    private String deleteAllNonLettersFromTextWithExceptions(String textString) throws NoWordsInTheChunkException{

        StringBuilder sb = new StringBuilder();

        boolean isSpace = false;

        for(char character: textString.toCharArray()){

            // we leave " " in, because separation of words by space is made in the next processing step,
            // here we swap the 'enter' and 'tab' character for " " so that words on the end of the line don't get stuck together
            if((character == 32) || (character == 13) || (character == 9)){

                if(!isSpace){
                    sb.append(" ");
                    isSpace = true;
                } else{
                    continue;
                }

                continue;
            }

            // if character is "-" or "'" then we leave them in, because those are usually a part of the word
            if((character == 39) || (character == 45)){
                sb.append(character);
                isSpace = false;
                continue;
            }

            if((character >= 65 && character <= 90) || (character >= 97 && character <= 122)){
                sb.append(character);
                isSpace = false;
            }
        }

        if(sb.toString().isBlank()){
            throw new NoWordsInTheChunkException();
        }

        return  sb.toString();
    }

    private String[] separateTextIntoWords(String textString){
       return textString.trim().split(" ");
    }

    // This method checks only the first letter of the word for being in upper case, because if we checked every letter
    // in the word then that would take enormously more time and rarely there are upper case letters in the middle of the word.
    // So I decided to trade a possible small amount of mistakes for faster processing time.
    private String[] wordArrayToLowerCase(String[] wordArray){

        for(int i = 0; i < wordArray.length; i++){

            char firstLetter = wordArray[i].charAt(0);

            if(firstLetter < 97){
                wordArray[i] = wordArray[i].toLowerCase();
            }
        }

        return wordArray;
    }

    private HashMap<String, Integer> processWordsIntoWordAndFrequencyPair(String[] wordArray, HashMap<String, Integer> givenMap){

        for(String word : wordArray){

            if(givenMap.containsKey(word)){
                givenMap.replace(word, givenMap.get(word) + 1);
            } else{
                givenMap.put(word, 1);
            }
        }

        return givenMap;
    }

    private HashMap<String, Integer> orderWordsByFrequency(HashMap<String, Integer> originalMap){

        List<Map.Entry<String, Integer>> list = new LinkedList<>(originalMap.entrySet());

        list.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        HashMap<String, Integer> sortedMap = new LinkedHashMap<>();

        for(Map.Entry<String, Integer> entry : list){
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    private String convertMapToString(HashMap<String, Integer> map){

        StringBuilder sb = new StringBuilder();
        sb.append("}");

        for(Map.Entry<String, Integer> entry : map.entrySet()){

            sb.append("{");

            sb.append(entry.getKey());
            sb.append(" : ");
            sb.append(entry.getValue());

            sb.append("}");

        }

        sb.append("{");

        return sb.toString();
    }

    private String readHeaders(String message){

        onePartFile = message.substring(37, 40);

        return message.substring(0, 36);
    }

    private String exctractTextChunkFromTheMessage(String message){
        // the two headers take 40 chars, so text itself starts on 41
        return message.substring(41);
    }

    private HashMap<String, Integer> convertStringToMap(String processedText){

        String[] wordAndFreqRaw = processedText.substring(2).split("}\\{");
        HashMap<String, Integer> map = new HashMap<>();

        for(String rawPair : wordAndFreqRaw){

            String[] separatedWordAndFreq = rawPair.split(" : ");
            map.put(separatedWordAndFreq[0], Integer.parseInt(separatedWordAndFreq[1]));

        }

        return map;
    }

    private void checkIfWholeFileHasBeenProcessed(TextEntity entity){

        if(entity.getTotal_chunk_amount() == entity.getProcessed_chunk_amount()){
            repo.updateStatus("READY", entity.getId());
        }
    }

    public void processText(String message) {

        String id = readHeaders(message);

        repo.updateStatus("PROCESSING", id);
        TextEntity entity = repo.findByStringId(id);

        String originalText = exctractTextChunkFromTheMessage(message);
        String[] cleanWordArray;

        try{
            cleanWordArray = cleanOriginalText(originalText);
        } catch (NoWordsInTheChunkException e){
            System.out.println(e.getMessage());
            repo.updateProcessedChunkAmount(entity.getProcessed_chunk_amount() + 1, id);
            return;
        }

        String processedText;

        if((onePartFile.equals("yes")) || (entity.getProcessedtext() == null)){
            processedText = insertWordsIntoMap(new HashMap<String, Integer>(), cleanWordArray);
        } else{
            HashMap<String, Integer> entityMap = convertStringToMap(repo.findByStringId(id).getProcessedtext());
            processedText = insertWordsIntoMap(entityMap, cleanWordArray);
        }

        repo.updateProcessedText(processedText, id);
        repo.updateProcessedChunkAmount(entity.getProcessed_chunk_amount() + 1, id);

        TextEntity freshEntity = repo.findByStringId(id);

        checkIfWholeFileHasBeenProcessed(freshEntity);

        System.out.println("GOT TILL END");
    }


}

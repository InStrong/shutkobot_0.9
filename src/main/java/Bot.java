import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    //private static final String SEND_SUGGESTION ="Пиши предложение зачина ниже" ;
    private SQLHandler sqlHandler = new SQLHandler();
    public final String FINISH="/punch";
    public final String SUGGEST="/suggest";
    public final String VOTE="/vote";
    public final String TOP="/top";
    public final String DEFAULT="/default";

    public String getBotToken() {
        return "748051573:AAESWaNIHmruxH1p8jS3VmUDfRWdWIKAzbw";
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        long chatId = message.getChatId();
        String firstName = message.getChat().getFirstName();
        String lastName = message.getChat().getLastName();
        String userName = message.getChat().getUserName();
        if (message != null && message.hasText()) {
            sqlHandler.connect();
            if (message.getText().equals("/start")) {
                registerUser(chatId,firstName,lastName,userName);
            }
            switch (message.getText()){
                case (FINISH):
                    changeState(chatId,FINISH);
                    sendMessageToUser(chatId,"Добей шутку: "+getCurrentJoke());
                    break;
                case (SUGGEST):
                    changeState(chatId,SUGGEST);
                    sendMessageToUser(chatId,"Пиши предложение зачина ниже");
                    break;
                case (VOTE):
                    changeState(chatId,VOTE);
                    break;
                case (TOP):
                    changeState(chatId,TOP);
                    break;
                    default:
                        break;
            }
//            if (message.getText().equals(FINISH)){
//                changeState(chatId,FINISH);
//            }
//            if (message.getText().equals(SUGGEST)){
//                changeState(chatId,SUGGEST);
//            }
//            if (message.getText().equals(VOTE)){
//                changeState(chatId,VOTE);
//            }
//            if (message.getText().equals(TOP)){
//                changeState(chatId,TOP);
//            }
            if (getState(chatId).equals(DEFAULT)){
                sendMessageToUser(chatId,"Ты не выбрал ничего. Нажми /");
            }

            else if (getState(chatId).equals(SUGGEST)){
                if (!message.getText().startsWith("/")) {
                    addSuggestion(chatId, message.getText());
                    sendMessageToUser(chatId, "Зачин принят к рассмотрению. Спасибо");
                    changeState(chatId,DEFAULT);
                }

            }
            else if (getState(chatId).equals(FINISH)){
                if (!message.getText().startsWith("/")) {
                    addPunchLine(chatId,message.getText(),getCurrentJokeId());
                    sendMessageToUser(chatId, "Добивка записана, можешь добавить еще!");

                }

            }

        }

        sqlHandler.disconnect();
    }

    private void sendMessageToUser(long chatId, String messageText) {
        SendMessage sendMessage = new SendMessage().setChatId(chatId);
        sendMessage.setText(messageText);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return "Шуткобот";
    }


    private void registerUser(long chatId, String firstName, String lastName, String userName){
        sqlHandler.registerUser(chatId,firstName,lastName,userName);
    }
    private void changeState(long chatId,String state){
        sqlHandler.changeState(chatId,state);
    }
    private String getState(long chatId){
        return sqlHandler.getState(chatId);
    }
    private void addSuggestion(long chatId,String text){
        sqlHandler.addSuggestion(chatId,text);
    }
    private String getCurrentJoke(){
        return sqlHandler.getCurrentJoke();
    }
    private int getCurrentJokeId(){
        return sqlHandler.getCurrentJokeId();
    }
    private void addPunchLine(long chatId,String text,int startId){
        sqlHandler.addPunchLine(chatId,text,startId);
    }



}
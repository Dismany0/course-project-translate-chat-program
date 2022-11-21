package views.TempSamStuff;

import atranslate_use_case.MessageTranslateData;
import atranslate_use_case.MessageTranslateInputBoundary;
import atranslate_use_case.MessageTranslateResponse;


import java.io.IOException;

public class MessageTranslateController {
    final MessageTranslateInputBoundary messageTranslateInputBoundary;

    public MessageTranslateController(MessageTranslateInputBoundary messageTranslateInputBoundary) {
        this.messageTranslateInputBoundary = messageTranslateInputBoundary;
    }

    MessageTranslateResponse translate(MessageTranslateData data) throws IOException {
        return this.messageTranslateInputBoundary.translate(data);
    }
}

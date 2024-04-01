package com.example.ocr;
import com.google.gson.annotations.SerializedName;

public class OCR {
    public OCRResult result;
    @SerializedName("modelVersion")
    public String version;

    public OCRResult getResult() {
        return result;
    }

    public void setResult(OCRResult result) {
        this.result = result;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

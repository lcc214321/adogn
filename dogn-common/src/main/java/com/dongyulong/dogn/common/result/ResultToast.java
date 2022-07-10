package com.dongyulong.dogn.common.result;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangshaolong
 * @create 2021/11/19
 **/
public class ResultToast extends BaseResult implements Serializable {

    /**
     * 提示内容
     */
    @JsonProperty("rich_message")
    private ToastMessage richMessage;

    /**
     * 提示类型
     */
    @JsonProperty("message_type")
    private int messageType;

    /**
     * 按钮信息
     */
    @JsonProperty
    private String button;

    public ToastMessage getRichMessage() {
        return richMessage;
    }

    public void setRichMessage(ToastMessage richMessage) {
        this.richMessage = richMessage;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public static class ToastMessage {
        private Integer type;
        private String title;
        private List<String> subtitles;
        private List<Button> buttons;
        private String image;
        /**
         * 控制提醒类样式
         * 0-常驻 5-5s 10-10s
         */
        private int duration;

        public ToastMessage() {
            this.duration = duration;
        }

        public ToastMessage(Integer type, String title, int duration) {
            this.type = type;
            this.title = title;
            this.duration = duration;
        }

        public ToastMessage(Integer type, String title, List<String> subtitles, List<Button> buttons, String image, int duration) {
            this.type = type;
            this.title = title;
            this.subtitles = subtitles;
            this.buttons = buttons;
            this.image = image;
            this.duration = duration;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getSubtitles() {
            return subtitles;
        }

        public void setSubtitles(List<String> subtitles) {
            this.subtitles = subtitles;
        }

        public List<Button> getButtons() {
            return buttons;
        }

        public void setButtons(List<Button> buttons) {
            this.buttons = buttons;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }

    /**
     * 按钮组
     */
    static class Button {
        private String title;
        private String link;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public Button() {
        }

        public Button(String title, String link) {
            this.title = title;
            this.link = link;
        }
        public Button(String title) {
            this.title = title;
        }
    }
}

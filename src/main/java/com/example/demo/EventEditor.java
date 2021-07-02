package com.example.demo;

import java.beans.PropertyEditorSupport;

// 서로 다른 Thread 에게 공유가 된다.
// Stateful. Thread-Not-Safe
// 여러 Thread 에 공유해서 쓰면 안된다.
// Thread-Scope(Bean) 일 때만 사용하라. Bean 등록 걍 하지마...
// Object-String 간의 변환만 가능하다.
public class EventEditor extends PropertyEditorSupport {
/*
    @Override
    public String getAsText() {
        Event event = (Event) getValue();
        return event.getId().toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(new Event(Integer.parseInt(text)));
    }

 */
}
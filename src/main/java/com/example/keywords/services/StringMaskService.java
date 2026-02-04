package com.example.keywords.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public interface StringMaskService {

    public String maskSensitiveWords(String input);
}
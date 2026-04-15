package com.dekapx.apps.agent;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class AgentTools {

    @Tool("Get the current date")
    public String getCurrentDate() {
        return "Today is: " + LocalDate.now();
    }

    @Tool("Get the current time")
    public String getCurrentTime() {
        return "Current time: " + LocalTime.now();
    }

    @Tool("Calculate the sum of two numbers")
    public double add(double a, double b) {
        return a + b;
    }

    @Tool("Get weather for a city (mock)")
    public String getWeather(String city) {
        return "Weather in " + city + ": 18°C, partly cloudy";
    }
}

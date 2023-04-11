package com.didges.school;

import java.util.*;

public class JavaSchoolStarter {
    private List<Map<String, Object>> data = new ArrayList<>();

    public JavaSchoolStarter() {

    }
    public JavaSchoolStarter(List<Map<String, Object>> data) {
        this.data = data;
    }

    public List<Map<String, Object>> execute(String command) {
        command = cleanFromNoNeedChars(command);
        littleConditionValidation(command);
        if (command.equals("SELECT")) {
            return data;
        }
        switch (command.substring(0, 6).toUpperCase()) {
            case "SELECT" -> {
                Select select = new Select();
                return select.select(command, data);
            }
            case "DELETE" -> {
                return delete(command);
            }
            case "INSERT" -> {
                return insert(command);
            }
            case "UPDATE" -> {
                return update(command);
            }
            default ->
                    throw new IllegalArgumentException("Неправильно введено первое слово запроса, должно быь в UPPER CASE, типа ,INSERT,DELETE,SELECT,UPDATE");
        }
    }

    private List<Map<String, Object>> insert(String condition) {
        String string = condition.substring(12, condition.length()).replace("=", ",");
        String[] massiveSplit = string.split(",");
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < massiveSplit.length - 1; i++) {
            map.put(massiveSplit[i], massiveSplit[i + 1]);
            i++;
        }
        data.add(map);
        return data;
    }

    private List<Map<String, Object>> update(String condition) {
        if (condition.contains("VALUES")) {
            condition = condition.substring(13, condition.length());
        } else if (!condition.contains("VALUES")) {
            condition = condition.substring(7, condition.length());
        }
        condition = cleanFromNoNeedChars(condition).replace("=", "").replace(",", "").replace("''", "'");
        String[] massive = condition.split("'");
        int iteratorInt = 0;
        for (int i = 0; i < massive.length - 1; i++) {
            if (massive[i].equals("WHERE")) {
                i += 1;
                for (Map<String, Object> map : data) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i])
                            && entry.getValue().toString().replace("'", "").equals(massive[i + 1])
                            && checkIfMapValuesAreNotAllEmpty(map)){
                            updateStepTwo(map, massive); // в которой лежит нужный айди
                        }
                    }
                }
            }
        }

        return data;
    }

    private void updateStepTwo(Map<String, Object> map, String[] massive) {
        for (int i = 0; i < massive.length - 1; i++) {
            if (!massive[i].equals("WHERE")) {
                for (Map<String, Object> datum : data) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i])) {
                            datum.replace(entry.getKey(), entry.getValue(), massive[i + 1]);
                        }
                    }
                }
            }
        }
    }

    private List<Map<String, Object>> delete(String condition) {
        if (condition.contains("WHERE")) {
            condition = condition.substring(12, condition.length());
        } else if (!condition.contains("WHERE")) {
            condition = condition.substring(6, condition.length());
        }
        condition = cleanFromNoNeedChars(condition);
        String[] massive = condition.split("'");
        ArrayList<Integer> resultOfIterationList = new ArrayList<>();
        for (int i = 0; i < massive.length - 1; i++) {
            if (massive[i + 1].contains("=")) {
                massive[i + 1] = massive[i + 1].replace("=", "");
            }
            for (Map<String, Object> map : data) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i])
                        && entry.getValue().toString().replace("'", "").equals(massive[i + 1])
                        && checkIfMapValuesAreNotAllEmpty(map)) {
                        resultOfIterationList.add(data.indexOf(map));
                    }
                }
            }
        }
        Collections.reverse(resultOfIterationList);
        for (Integer integer : resultOfIterationList) {
            int i = integer;
            data.remove(i);
        }
        return data;
    }

    private void littleConditionValidation(String condition) {
        if (condition.contains("null")) {
            throw new IllegalArgumentException("Значения числовых переменных не может быть null");
        }
        if (condition.isEmpty() || condition.isBlank()) {
            throw new IllegalArgumentException("Запрос не может быть пустым!");
        }
    }

    private boolean checkCondition(String condition) {
        // реализация метода для проверки условия выборки
        return false;
    }

    private String cleanFromNoNeedChars(String string) {
        string = string.replace(" ", "").replace("or", ",").replace("AND", "&&").replace("values","VALUES")
                .replace("OR", ",").replace("AD", "").replace("and", "&&").replace("null","0").replace("(","")
                .replace("%", "").replace("where", "WHERE").replace("‘", "'").replace("`", "'").replace(")","");
        ;
        return string;
    }

    protected boolean checkIfMapValuesAreNotAllEmpty(Map<String, Object> map) {
        int size = map.size();
        int counter = 0;
        for (Object value : map.values()) {
            if (value.toString().replace("'", "").isEmpty()) {
                counter = +1;
            }
        }
        if (counter == size) {
            throw new IllegalArgumentException("Все значения не могут быть пустыми");
        }
        return true;
    }
}
package com.didges.school;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Select extends JavaSchoolStarter {
    private final List<Map<String, Object>> listToReturn = new ArrayList<>();
    List<Map<String, Object>> select(String condition, List<Map<String, Object>> data) {
        if (condition.contains("WHERE")) {
            condition = condition.substring(12, condition.length()).replace(",", "");
        } else {
            condition = condition.substring(6, condition.length()).replace(",", "");
        }
        String[] massive = condition.split("'");
        //если стринг
        for (int i = 0; i < massive.length; i++) {
            if (massive[i].contains("like") || massive[i].contains("ilike")) {
                likeChecking(massive, data);
            }
            //если переменная
            //можно сделать так, чтобы они возвращали новое значение без этого оператора, чтобы цикл мог идти дальше, либо можно итерироваться по массиву
            if (massive[i].contains("<=") || massive[i].contains(">=") || massive[i].contains("!=") || massive[i].contains(">") || massive[i].contains("<") || massive[i].contains("=")) {
                checkingForHavingOperatorsInt(massive, data);
            }
            //если стринг
            if (massive[i].contains("!=") || massive[i].contains("=")) {
                havingOperatorsString(massive, data);
            }
        }
        return listToReturn;
    }

    private void havingOperatorsString(String[] massive, List<Map<String, Object>> data) {
        for (int i = 0; i < massive.length - 3; i++) {
            if (massive[i + 3].equals("&&")) {
                if (massive[i + 1].equals("!=")) {
                    massive[i + 1] = massive[i + 1].replace("!=", "");
                    for (Map<String, Object> map : data) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i])
                                && !entry.getValue().toString().replace("'", "").equals(massive[i + 2])
                                && havingOperatorsStringHelper(massive, i + 5, data, map)
                                && checkIfMapValuesAreNotAllEmpty(map)) {
                                listToReturn.add(data.get(data.indexOf(map)));
                            }
                        }
                    }
                }
                if (massive[i + 1].equals("=")) {
                    massive[i + 1] = massive[i + 1].replace("=", "");
                    for (Map<String, Object> map : data) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i])
                                && entry.getValue().toString().replace("'", "").equals(massive[i + 2])
                                && havingOperatorsStringHelper(massive, i + 5, data, map)
                                && checkIfMapValuesAreNotAllEmpty(map)) {
                                listToReturn.add(data.get(data.indexOf(map)));
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < massive.length - 3; i++) {
            if (!massive[i + 3].equals("&&")) {
                if (massive[i + 1].equals("!=")) {
                    massive[i + 1] = massive[i + 1].replace("!=", "");
                    for (Map<String, Object> map : data) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i])
                                && !entry.getValue().toString().replace("'", "").equals(massive[i + 2])
                                && checkIfMapValuesAreNotAllEmpty(map)) {
                                listToReturn.add(data.get(data.indexOf(map)));
                            }
                        }
                    }
                }
                if (massive[i + 3].equals("=")) {
                    massive[i + 1] = massive[i + 1].replace("=", "");
                    for (Map<String, Object> map : data) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i])
                                && entry.getValue().toString().replace("'", "").equals(massive[i + 2])
                                && checkIfMapValuesAreNotAllEmpty(map)) {
                                listToReturn.add(data.get(data.indexOf(map)));
                            }
                        }
                    }
                }
            }
        }
    }


    private boolean havingOperatorsStringHelper(String[] massive, int i, List<Map<String, Object>> data, Map<String, Object> map) {
        if (massive[i].equals("!=")) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                        && !entry.getValue().toString().replace("'", "").equals(massive[i + 1])
                        && checkIfMapValuesAreNotAllEmpty(map)) {
                        massive[i] = massive[i].replace("!=", "");
                        return true;
                    }
            }
        }
            if (massive[i].equals("=")) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                            && entry.getValue().toString().replace("'", "").equals(massive[i + 1])
                            && checkIfMapValuesAreNotAllEmpty(map)) {
                            massive[i] = massive[i].replace("=", "");
                            return true;
                        }
                }
        }
        return false;
    }

    private void likeChecking(String[] massive, List<Map<String, Object>> data) {
        for (int i = 0; i < massive.length - 1; i++) {
            if (massive[i + 1].contains("ilike")) {
                massive[i + 2] = massive[i + 2].replace("A", "");
                for (Map<String, Object> map : data) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i])
                            && entry.getValue().toString().replace("'", "").toLowerCase().contains(massive[i + 2].toLowerCase().replace("%", ""))
                            && checkIfMapValuesAreNotAllEmpty(map)) {
                            listToReturn.add(data.get(data.indexOf(map)));
                            massive[i + 1] = massive[i + 1].replace("ilike", "");
                        }
                    }
                }
            }
            if (massive[i + 1].contains("like")) {
                massive[i + 2] = massive[i + 2].replace("A", "");
                for (Map<String, Object> map : data) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i])
                            && entry.getValue().toString().replace("'", "").contains(massive[i + 2].replace("%", ""))
                            && checkIfMapValuesAreNotAllEmpty(map)) {
                            listToReturn.add(data.get(data.indexOf(map)));
                            massive[i + 1] = massive[i + 1].replace("like", "");
                        }
                    }
                }
                //можно подумать на счет i += 2
            }
        }
    }

    //основной
    private void checkingForHavingOperatorsInt(String[] massive, List<Map<String, Object>> data) {
        for (int i = 0; i < massive.length; i++) {
            if (!massive[i].contains("&&")) {
                if (massive[i].contains("<=")) {
                    massive[i] = massive[i].replace("<=", "");
                    int f = Integer.parseInt(massive[i]);
                    for (Map<String, Object> map : data) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                                && checkIfMapValuesAreNotAllEmpty(map)) {
                                int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                                if (l <= f) {
                                    listToReturn.add(data.get(data.indexOf(map)));
                                }
                            }
                        }
                    }
                }
                if (massive[i].contains(">=")) {
                    massive[i] = massive[i].replace(">=", "");
                    int f = Integer.parseInt(massive[i]);
                    for (Map<String, Object> map : data) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                                && checkIfMapValuesAreNotAllEmpty(map)) {
                                int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                                if (l >= f) {
                                    listToReturn.add(data.get(data.indexOf(map)));
                                }
                            }
                        }
                    }
                }
                if (massive[i].contains("!=")) {
                    try {
                        int f = Integer.parseInt(massive[i].replace("!=", ""));
                        for (Map<String, Object> map : data) {
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                                    && checkIfMapValuesAreNotAllEmpty(map)) {
                                    int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                                    if (l != f) {
                                        listToReturn.add(data.get(data.indexOf(map)));
                                    }
                                }
                            }
                        }
                    } catch (NumberFormatException ignored) {
                        return;
                    }
                }
                if (massive[i].contains("=")) {
                    try {
                        int f = Integer.parseInt(massive[i].replace("=", ""));
                        for (Map<String, Object> map : data) {
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                                    && checkIfMapValuesAreNotAllEmpty(map)) {
                                    int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                                    if (l == f) {
                                        listToReturn.add(data.get(data.indexOf(map)));
                                    }
                                }
                            }
                        }
                    } catch (NumberFormatException ignored) {
                        return;
                    }
                }
                if (massive[i].contains(">")) {
                    massive[i] = massive[i].replace(">", "");
                    int f = Integer.parseInt(massive[i]);
                    for (Map<String, Object> map : data) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                                && checkIfMapValuesAreNotAllEmpty(map)) {
                                int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                                if (l > f) {
                                    listToReturn.add(data.get(data.indexOf(map)));
                                }
                            }
                        }
                    }
                }
                if (massive[i].contains("<")) {
                    massive[i] = massive[i].replace("<", "");
                    int f = Integer.parseInt(massive[i]);
                    for (Map<String, Object> map : data) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                                && checkIfMapValuesAreNotAllEmpty(map)) {
                                int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                                if (l < f) {
                                    listToReturn.add(data.get(data.indexOf(map)));
                                }
                            }
                        }
                    }
                }
            }
            if (massive[i].contains("&&")) {
                massive[i] = massive[i].replace("&&", "");
                if (massive[i].contains("<=")) {
                    massive[i] = massive[i].replace("<=", "");
                    int f = Integer.parseInt(massive[i]);
                    for (Map<String, Object> map : data) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                                && checkIfMapValuesAreNotAllEmpty(map)) {
                                int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                                if (l <= f && ifMassiveHasAndInItAndItsInt(massive, i + 2, data)) {
                                    listToReturn.add(data.get(data.indexOf(map)));
                                }
                            }
                        }
                    }
                }
                if (massive[i].contains(">=")) {
                    massive[i] = massive[i].replace(">=", "");
                    int f = Integer.parseInt(massive[i]);
                    for (Map<String, Object> map : data) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                                && checkIfMapValuesAreNotAllEmpty(map)) {
                                int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                                if (l >= f && ifMassiveHasAndInItAndItsInt(massive, i + 2, data)) {
                                    listToReturn.add(data.get(data.indexOf(map)));
                                }
                            }
                        }
                    }
                }
                if (massive[i].contains("!=")) {
                    massive[i] = massive[i].replace("!=", "");
                    int f = Integer.parseInt(massive[i]);
                    for (Map<String, Object> map : data) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                                && checkIfMapValuesAreNotAllEmpty(map)) {
                                int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                                if (l != f && ifMassiveHasAndInItAndItsInt(massive, i + 2, data)) {
                                    listToReturn.add(data.get(data.indexOf(map)));
                                }
                            }
                        }
                    }
                }
                if (massive[i].contains("=")) {
                    massive[i] = massive[i].replace("=", "");
                    int f = Integer.parseInt(massive[i]);
                    for (Map<String, Object> map : data) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                                && checkIfMapValuesAreNotAllEmpty(map)) {
                                int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                                if (l == f && ifMassiveHasAndInItAndItsInt(massive, i + 2, data)) {
                                    listToReturn.add(data.get(data.indexOf(map)));
                                }
                            }
                        }
                    }
                }
                if (massive[i].contains(">")) {
                    massive[i] = massive[i].replace(">", "");
                    int f = Integer.parseInt(massive[i]);
                    for (Map<String, Object> map : data) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                                && checkIfMapValuesAreNotAllEmpty(map)) {
                                int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                                if (l > f && ifMassiveHasAndInItAndItsInt(massive, i + 2, data)) {
                                    listToReturn.add(data.get(data.indexOf(map)));
                                }
                            }
                        }
                    }
                }
                if (massive[i].contains("<")) {
                    massive[i] = massive[i].replace("<", "");
                    int f = Integer.parseInt(massive[i]);
                    for (Map<String, Object> map : data) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                                && checkIfMapValuesAreNotAllEmpty(map)) {
                                int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                                if (l < f && ifMassiveHasAndInItAndItsInt(massive, i + 2, data)) {
                                    listToReturn.add(data.get(data.indexOf(map)));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //вспомогательный
    private boolean ifMassiveHasAndInItAndItsInt(String[] massive, int i, List<Map<String, Object>> data) {
        //нам надо найти два условия между || i-1 i i+1 i + 2;
        //можно взять чисто два этих условия между которыми || и запустить их в цикл, выделить в отдельный массив
        if (massive[i].contains("<=")) {
            massive[i] = massive[i].replace("<=", "");
            int f = Integer.parseInt(massive[i]);
            for (Map<String, Object> map : data) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                        && checkIfMapValuesAreNotAllEmpty(map)) {
                        int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                        if (l <= f) {
                            return true;
                        }
                    }
                }
            }
        }
        if (massive[i].contains(">=")) {
            massive[i] = massive[i].replace(">=", "");
            int f = Integer.parseInt(massive[i]);
            for (Map<String, Object> map : data) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                        && checkIfMapValuesAreNotAllEmpty(map)) {
                        int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                        if (l >= f) {
                            return true;
                        }
                    }
                }
            }
        }
        if (massive[i].contains("!=")) {
            massive[i] = massive[i].replace("!=", "");
            int f = Integer.parseInt(massive[i]);
            for (Map<String, Object> map : data) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                        && checkIfMapValuesAreNotAllEmpty(map)) {
                        int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                        if (l != f) {
                            return true;
                        }
                    }
                }
            }
        }
        if (massive[i].contains("=")) {
            massive[i] = massive[i].replace("=", "");
            int f = Integer.parseInt(massive[i]);
            for (Map<String, Object> map : data) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                        && checkIfMapValuesAreNotAllEmpty(map)) {
                        int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                        if (l == f) {
                            return true;
                        }
                    }
                }
            }
        }
        if (massive[i].contains(">")) {
            massive[i] = massive[i].replace(">", "");
            int f = Integer.parseInt(massive[i]);
            for (Map<String, Object> map : data) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                        && checkIfMapValuesAreNotAllEmpty(map)) {
                        int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                        if (l > f) {
                            return true;
                        }
                    }
                }
            }
        }
        if (massive[i].contains("<")) {
            massive[i] = massive[i].replace("<", "");
            int f = Integer.parseInt(massive[i]);
            for (Map<String, Object> map : data) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().replace("'", "").equalsIgnoreCase(massive[i - 1])
                        && checkIfMapValuesAreNotAllEmpty(map)) {
                        int l = Integer.parseInt(entry.getValue().toString().replace("'", ""));
                        if (l < f) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
//Можно заменить проверенные значения на true или false в condition, чтобы проходить по запросу с данными && было легче

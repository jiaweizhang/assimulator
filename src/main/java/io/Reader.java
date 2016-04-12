package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by jiaweizhang on 4/7/16.
 */
public class Reader {
    public List<String> readFile(String fileName) throws FileNotFoundException {
        Scanner sc = new Scanner(new BufferedReader(new FileReader(fileName)));

        List<String> lines = new ArrayList<String>();
        while (sc.hasNextLine()) {
            lines.add(sc.nextLine());
        }
        return lines;
    }
}

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


public class FileParsing {
    static String fileNameInt = "integers.txt";
    static String fileNameDouble = "floats.txt";
    static String fileNameStr = "strings.txt";
    static String[] arrayKey = {"-o", "-p", "-s", "-f", "-a"};
    static boolean keyStatFull = false;
    static boolean keyStatShort = false;
    static boolean keyAppendFile = false;
    static String paramDir = "";
    static String paramPrefix = "";
    static int iMax, iMin, iSum, sMax, sMin;
    static double dMax, dMin, dSum;
    static List<String> InputFileList = new ArrayList<>();

    static void updateStatsInt(List intLines, int intValue) {
        if (keyStatFull) {
            if (intLines.size() == 1) iMax = iMin = intValue;
            else {
                if (intValue > iMax) iMax = intValue;
                if (intValue < iMin) iMin = intValue;
            }
            iSum += intValue;
        }
    }

    static void updateStatsDouble(List doubleLines, double doubleValue) {
        if (keyStatFull) {
            if (doubleLines.size() == 1) dMax = dMin = doubleValue;
            else {
                if (doubleValue > dMax) dMax = doubleValue;
                if (doubleValue < dMin) dMin = doubleValue;
            }
            dSum += doubleValue;
        }
    }

    static void updateStatsLines(List textLines, String line) {
        if (textLines.size() == 1) sMax = sMin = line.length();
        else {
            if (line.length() > sMax) sMax = line.length();
            if (line.length() < iMin) sMin = line.length();
        }
    }

    public static void main(String[] args) {
        String paramKey = "";
        String currentDir;

        if (args.length == 0) {
            System.out.println("Комманда запуска: java FileParsing -o </some/path> -p <prefixNameFile_> -s -f -a <inputfile1>...<inputfileN>");
            return;
        }

        for (String arg : args) {
            if (Arrays.asList(arrayKey).contains(arg)) {
                paramKey = arg;
                if (arg.equals("-s")) keyStatShort = true;
                else if (arg.equals("-f")) keyStatFull = true;
                else if (arg.equals("-a")) keyAppendFile = true;
            } else if (paramKey.equals("-o")) {
                String directoryPath = arg;
                File file = new File(directoryPath);
                if (file.isDirectory()) {
                    paramDir = arg;
                } else {
                    System.out.println("Incorrect parameter -o (ignore) don't Directory: " + arg);
                }
                paramKey = "";
            } else if (paramKey.equals("-p")) {
                paramPrefix = arg;
                paramKey = "";
            } else {
                File file = new File(arg);
                if (file.exists()) {
                    InputFileList.add(arg);
                } else {
                    System.out.println("Incorrect parameter (ignore) don't file: " + arg);
                }
            }
        }

        if (InputFileList.isEmpty()) {
            System.out.println("Incorrect parameter: Не указаны входные файлы.");
            return;
        }
        currentDir = System.getProperty("user.dir");
        if (paramDir.isEmpty()) {
            paramDir = currentDir;
        }
        fileNameInt = paramDir + "\\" + paramPrefix + fileNameInt;
        fileNameDouble = paramDir + "\\" + paramPrefix + fileNameDouble;
        fileNameStr = paramDir + "\\" + paramPrefix + fileNameStr;


        //2. Разбор входных файлов и подсчёт статистики
        List<String> textLines = new ArrayList<>();
        List<Integer> intLines = new ArrayList<>();
        List<Double> doubleLines = new ArrayList<>();
        for (String inputFile : InputFileList) {
            try (BufferedReader br = new BufferedReader(new FileReader(currentDir + "\\" + inputFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    try {
                        int intValue = Integer.parseInt(line);
                        intLines.add(intValue);
                        updateStatsInt(intLines, intValue);
                    } catch (NumberFormatException e1) {
                        try {
                            double doubleValue = Double.parseDouble(line);
                            doubleLines.add(doubleValue);
                            updateStatsDouble(doubleLines, doubleValue);
                        } catch (NumberFormatException e2) {
                            textLines.add(line);
                            updateStatsLines(textLines, line);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //3. Вывод статистики
        if (keyStatShort) {
            System.out.println("Report String count=" + textLines.size());
            System.out.println("Report Integer count=" + intLines.size());
            System.out.println("Report double count=" + doubleLines.size());
        }
        if (keyStatFull) {
            if (!intLines.isEmpty()) {
                System.out.println("Report Integer Min=" + iMin);
                System.out.println("Report Integer Max=" + iMax);
                System.out.println("Report Integer Sum=" + iSum);
                System.out.println("Report Integer Average=" + (double) iSum / intLines.size());
            }
            if (!doubleLines.isEmpty()) {
                System.out.println("Report double Min=" + dMin);
                System.out.println("Report double Max=" + dMax);
                System.out.println("Report double Sum=" + dSum);
                System.out.println("Report double Average=" + dSum / doubleLines.size());
            }
            if (!textLines.isEmpty()) {
                System.out.println("Report string Min=" + sMin);
                System.out.println("Report string Max=" + sMax);
            }
        }

        //4. Запись в выходные файлы
        if (!textLines.isEmpty()) {
            try (PrintWriter pWriter = new PrintWriter(new FileOutputStream(fileNameStr, keyAppendFile))) {
                for (String line : textLines) {
                    pWriter.println(line);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (!intLines.isEmpty()) {
            try (PrintWriter pWriter = new PrintWriter(new FileOutputStream(fileNameInt, keyAppendFile))) {
                if (!intLines.isEmpty()) {
                    for (int value : intLines) {
                        pWriter.println(value);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (!doubleLines.isEmpty()) {
            try (PrintWriter pWriter = new PrintWriter(new FileOutputStream(fileNameDouble, keyAppendFile))) {
                if (!doubleLines.isEmpty()) {
                    for (double value : doubleLines) {
                        pWriter.println(value);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
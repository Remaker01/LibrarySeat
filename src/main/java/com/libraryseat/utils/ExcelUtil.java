package com.libraryseat.utils;

import com.libraryseat.pojo.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelUtil {
    private static final Pattern PSWD_REGEX = Pattern.compile("^(?![\\d]+$)(?![a-zA-Z]+$)(?![^\\da-zA-Z]+$).{7,20}$");
    private static final Pattern UNAME_REGEX = Pattern.compile("^[a-zA-Z]\\w{4,19}$");
    private static final HashMap<String,Short> roleMap = new HashMap<>(2);
    private static Workbook getWorkbook(InputStream stream,String format) throws IOException,IllegalArgumentException {
        if (format.equalsIgnoreCase("xls")){
            Workbook workbook = new HSSFWorkbook(stream,true);
            workbook.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            return workbook;
        } else if (format.equalsIgnoreCase("xlsx")||format.equalsIgnoreCase("xlsm")) {
            Workbook workbook = new XSSFWorkbook(stream);
            workbook.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            return workbook;
        }
        throw new IllegalArgumentException(format);
    }

    private static String getCellValue(Row row,int column){
        if (column < 0)
            return null;
        Cell cell = row.getCell(column);
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }

    private static String getPassword(String original){
        if (original == null)
            return null;
        Matcher matcher = PSWD_REGEX.matcher(original);
        if (!matcher.matches())
            return null;
        byte[] pswd_md5 = EncryptUtil.md5(EncryptUtil.md5(original.getBytes(StandardCharsets.ISO_8859_1)));
        return EncryptUtil.byteArrayToHexString(pswd_md5);
    }

    private static String getGender(String original){
        if (original == null)
            return null;
        if (original.equals("男")||original.equals("女"))
            return original;
        return null;
    }

    private static String getPhone(String original){
        if (original == null)
            return null;
        if (VerifyUtil.verifyPhone(original))
            return original;
        return null;
    }
    /**
     * 从包含excel文件的流中解析用户信息。行格式{用户名，密码，姓名，性别，手机号，身份}
     * @param stream 包含excel文件的stream
     * @param format 格式。支持xls,xlsx,xlsm
     * @return 解析得到的用户信息。自动过滤有问题的信息
     * @throws IOException 解析过程中出错
     * @throws IllegalArgumentException 不支持的格式
     */
    public static List<User> getUsersInWorkbook(InputStream stream,String format) throws IOException,IllegalArgumentException{
        if (roleMap.isEmpty()){
            roleMap.put("阅览室管理员",(short)1);
            roleMap.put("学生",(short)2);
        }
        Sheet sheet = getWorkbook(stream, format).getSheetAt(0);
        if (sheet == null)
            return new ArrayList<>(0);
        int firstRow = sheet.getFirstRowNum(),lastRow = sheet.getLastRowNum();
        List<User> result = new ArrayList<>(lastRow-firstRow);
        for(int i = firstRow; i <= lastRow; i++){
            Row row = sheet.getRow(i); //获取第i行数据
            if (row == null)
                continue;
            User u = new User();
            String uname = getCellValue(row,0);
            if (uname!=null&&UNAME_REGEX.matcher(uname).matches())
                u.setUsername(uname);
            String pswd = getPassword(getCellValue(row,1));
            u.setPassword(pswd);
            u.setTruename(getCellValue(row,2));
            String gender = getGender(getCellValue(row,3));
            u.setGender(gender);
            String phone = getPhone(getCellValue(row,4));
            u.setPhone(phone);
            Short role = roleMap.get(getCellValue(row,5));
            if (role!=null&&VerifyUtil.verifyNonEmptyStrings(u.getUsername(),
                    u.getPassword(),
                    u.getTruename(),
                    u.getGender(),
                    u.getPhone())) {
                u.setRole(role);
                result.add(u);
            }
        }
        return result;
    }
    public static String detectFormatViaMIME(String mime){
        if (mime == null)
            return "";
        mime = mime.toLowerCase(Locale.getDefault());
        if (mime.equals("application/vnd.ms-excel"))
            return "xls";
        if (mime.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml"))
            return "xlsx";
        if (mime.startsWith("application/vnd.ms-excel.sheet.macroenabled"))
            return "xlsm";
        return "";
    }
}

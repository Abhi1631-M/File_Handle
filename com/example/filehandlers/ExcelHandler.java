package com.example.filehandlers;

//import org.apache.poi.ss.formula.SheetIdentifier;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
//import java.util.stream.Collectors;

public class ExcelHandler implements IFileHandler<User>{
    private static final String SHEET_NAME="Users";
    private static final DataFormatter dataFormatter = new DataFormatter();

    @Override
    public void create(File file, List<User> data) throws IOException {

        try(Workbook workbook=new XSSFWorkbook();
             FileOutputStream out=new FileOutputStream(file))
         {
             Sheet sheet=workbook.createSheet(SHEET_NAME);
             int rowNum=0;
             Row headerRow=sheet.createRow(rowNum++);
             String[] headers={"emp_id", "name", "age","email"};
             for (int i=0;i< headers.length;i++)
             {
                 headerRow.createCell(i).setCellValue(headers[i]);
             }
             for(User user:data)
             {
                 Row row=sheet.createRow(rowNum++);
                 row.createCell(0).setCellValue(user.getEmp_id());
                 row.createCell(1).setCellValue(user.getName());
                 row.createCell(2).setCellValue(user.getAge());
                 row.createCell(3).setCellValue(user.getAge());
             }
             workbook.write(out);
         }

    }




    @Override
    public List<User> read(File file) throws IOException {
        List<User> users=new ArrayList<>();
        if(!file.exists())
        {
            return users;
        }
        try (FileInputStream fis=new FileInputStream(file);
              Workbook workbook=new XSSFWorkbook(fis)){
            Sheet sheet=workbook.getSheet(SHEET_NAME);
            if(sheet==null)
            {
                return users;
            }
            Iterator<Row> rowIterator=sheet.iterator();
            if(rowIterator.hasNext())
            {
                rowIterator.next();
            }
            while (rowIterator.hasNext())
            {
                Row row=rowIterator.next();
                User user=fromRow(row);
                if (user != null)
                {
                    users.add(user);
                }
            }
        }
        return users;

    }


    @Override
    public Optional<User> read(File file, String identifier) throws IOException {
        return read(file).stream()
                .filter(user -> ((User)user).getEmp_id().equals(identifier))
                .findFirst();
    }

    @Override
    public boolean update(File file, User updateData, String identifier) throws IOException {
        List<User> users=read(file);
        boolean found=false;
        for(int i=0;i<users.size();i++)
        {
            if(users.get(i).getEmp_id().equals(identifier))
            {
                users.set(i,updateData);
                found=true;
                break;
            }
        }
        if (found)
        {
            create(file,users);
        }
        return found;
    }



    @Override
    public boolean delete(File file, String identifier) throws IOException {
       List<User>users=read(file);
       long initialCount=users.size();
       List<User>remainingUsers=users.stream()
               .filter(user -> !user.getEmp_id().equals(identifier))
               .collect(ArrayList::new,List::add,List::addAll);
       if(remainingUsers.size()<initialCount)
       {
           create(file,remainingUsers);
           return true;
       }
        return false;
    }
    private User fromRow(Row row)
    {
        if(row.getCell(0)==null)
            return null;
        String id=dataFormatter.formatCellValue(row.getCell(0));
        String name=dataFormatter.formatCellValue(row.getCell(1));
        int age = row.getCell(2) != null ? (int) row.getCell(2).getNumericCellValue() : 0;
        String email = dataFormatter.formatCellValue(row.getCell(3));
        return new User(id, name, age, email);
    }
}

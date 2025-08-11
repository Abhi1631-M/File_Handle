package com.example.filehandlers;

import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class PropertiesHandle implements IFileHandler<User> {
    @Override
    public void create(File file, List<User> data) throws IOException {
        Properties properties=new Properties();
        for(User user:data)
        {
            properties.setProperty(user.getEmp_id()+".name", user.getName());
            properties.setProperty(user.getEmp_id()+".age",String.valueOf(user.getAge()));
            properties.setProperty(user.getEmp_id()+".email", user.getEmail() );
        }
        try(OutputStream outputStream=new FileOutputStream(file))
        {
            properties.store(outputStream,"User Properties");
        }
    }

    @Override
    public List<User> read(File file) throws IOException {
        if(!file.exists())
        {
            return Collections.emptyList();
        }
        Properties properties=new Properties();
        try(InputStream in =new FileInputStream(file)) {
             properties.load(in);
        }
        return properties.stringPropertyNames().stream().filter(key->key.endsWith(".name"))
                .map(key->{
                    String id=key.substring(0,key.indexOf("."));
                    String name=properties.getProperty(id+".name");
                    int age=Integer.parseInt(properties.getProperty(id+".age"));
                    String email=properties.getProperty(id+".email");
                    return new User(id,name, age, email);
                })
                .collect(java.util.ArrayList::new, List::add,List::addAll);
    }

    @Override
    public Optional<User> read(File file, String identifier) throws IOException {
        if(!file.exists())
        {
            return Optional.empty();
        }
        Properties properties=new Properties();
        try(InputStream in =new FileInputStream(file)) {
            properties.load(in);

        }
        if(properties.containsKey(identifier+".name"))
        {
            String name=properties.getProperty(identifier+".name");
            int age=Integer.parseInt(properties.getProperty(identifier+".age"));
            String email=properties.getProperty(identifier+".email");
            return Optional.of(new User(identifier,name,age,email));
        }
        return Optional.empty();

    }

    @Override
    public boolean update(File file, User updateData, String identifier) throws IOException {
        Properties properties=new Properties();
        if(file.exists()) {
            try (InputStream in = new FileInputStream(file)) {
                properties.load(in);

            }
        }
            if(properties.containsKey(identifier+".name"))
            {
                properties.setProperty(identifier+".name",updateData.getName());
                properties.setProperty(identifier+".age",String.valueOf(updateData.getAge()));
                properties.setProperty(identifier+".email",updateData.getEmail());
                try(OutputStream ou=new FileOutputStream(file)) {
                   properties.store(ou,"Updated User properties");
                }
                return true;
            }

        return false;
    }

    @Override
    public boolean delete(File file, String identifier) throws IOException {
        Properties properties=new Properties();
        if(file.exists())
        {
            try (InputStream in =new FileInputStream(file)){
                properties.load(in);

            }

        }
        if(properties.containsKey(identifier+".name"))
        {
            properties.remove(identifier+".name");
            properties.remove(identifier+".age");
            properties.remove(identifier+".email");
            try (OutputStream out=new FileOutputStream(file)){
                properties.store(out,"User properties with deletion");

            }
            return true;
        }
        return false;
    }
}

package com.example.filehandlers;


import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class UserList{
    private List<User> users=new ArrayList<>();
    public List<User> getUsers()
    {
        return users;
    }
    public void setUsers(List<User>users)
    {
        this.users=users;
    }
}
public class XmlHandler implements IFileHandler<User> {


    @Override
    public void create(File file, List<User> data) throws IOException {
        try {
            JAXBContext context=JAXBContext.newInstance(UserList.class,User.class);
            Marshaller marshaller= context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
            UserList userList=new UserList();
            userList.setUsers(data);
            marshaller.marshal(userList,file);
        } catch (JAXBException e) {
            throw new IOException("Error creating XML file",e);
        }
    }

    @Override
    public List<User> read(File file) throws IOException {
        if(!file.exists())
        {
            return new ArrayList<>();
        }
        try {
            JAXBContext context=JAXBContext.newInstance(UserList.class, User.class);
            Unmarshaller unmarshaller=context.createUnmarshaller();
            UserList userList=(UserList) unmarshaller.unmarshal(file);
            return userList.getUsers();
        } catch (JAXBException e) {
            throw new IOException("Error reading XML file",e);
        }
    }

    @Override
    public Optional<User> read(File file, String identifier) throws IOException {
        return read(file).stream()
                .filter(user -> user.getEmp_id().equals(identifier))
                .findFirst();
    }

    @Override
    public boolean update(File file, User updateData, String identifier) throws IOException {
        List<User> users=read(file);
        List<User> updatedUsers=users.stream().map(
                user -> user.getEmp_id().equals(identifier)?updateData:user)
                .collect(Collectors.toList());
        if(!users.equals(updatedUsers))
        {
            create(file,updatedUsers);
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(File file, String identifier) throws IOException {
       List<User> users=read(file);
       long initialCount=users.size();
       List<User> remainingUsers=users.stream()
               .filter(user -> !user.getEmp_id().equals(identifier))
               .collect(Collectors.toList());
       if(remainingUsers.size()<initialCount)
       {
           create(file,remainingUsers);
           return true;
       }
        return false;
    }
}

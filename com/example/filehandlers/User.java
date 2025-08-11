package com.example.filehandlers;

import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class User {
    private String emp_id;
    private String name;
    private  int age;
    //. private long emp_id;
    private  String email;
}

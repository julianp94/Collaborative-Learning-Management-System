package de.hohenheim.model;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
public class SopraUser {

    public enum Gender{
        unknown,
        male,
        female;
    }

    private String username = "";

    @Id
    @GeneratedValue
    private Integer id;

    private String emailAdress = "";

    private String firstName = "";

    private String lastName = "";

    private Gender gender = Gender.unknown;

    private Date birthDate = new Date((long)0);

    private String city = "";

    private String postalCode = "";

    private String streetName = "";

    private String streetNumber = "";

    private String telefonNumber = "";

    private String[] hobbies = new String[0];

    @ManyToMany
    @JoinTable(
            name="UserFeedAssignment",
            joinColumns=@JoinColumn(name="USER_ID"),
            inverseJoinColumns=@JoinColumn(name="COMMENT_ID"))
    private List<UserFeedEntry> userFeed = new ArrayList<UserFeedEntry>();

    public String getUsername() { return username; }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmailAdress() {
        return emailAdress;
    }

    public void setEmailAdress(String emailAdress) {
        this.emailAdress = emailAdress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String printBirthDate(){
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        return ft.format(birthDate);
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void parseBirthDate(String birthDate){
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.birthDate = ft.parse(birthDate);
        }catch(ParseException e){

        }
    }

    public String getTelefonNumber() {
        return telefonNumber;
    }

    public void setTelefonNumber(String telefonNumber) {
        this.telefonNumber = telefonNumber;
    }

    public String[] getHobbies() {
        return hobbies;
    }

    public void setHobbies(String[] hobbies) {
        this.hobbies = hobbies;
    }

    public void addHobbies (String hobby) {
        List<String> hobbyList = new ArrayList<String>(Arrays.asList(hobbies));
        if(!hobbyList.contains(hobby))
            hobbyList.add(hobby);
        hobbies = hobbyList.toArray(new String[hobbyList.size()]);
    }

    public void removeHobbies (String hobby){
        List<String> hobbyList = new ArrayList<String>(Arrays.asList(hobbies));
        hobbyList.remove(hobby);
        hobbies = hobbyList.toArray(new String[hobbyList.size()]);
    }

    public List<String> getHobbiesAsList(){
        return new ArrayList<String>(Arrays.asList(hobbies));
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public List<UserFeedEntry> getUserFeed() {
        return userFeed;
    }

    public void setUserFeed(List<UserFeedEntry> userFeed) {
        this.userFeed = userFeed;
    }

    public void addUserFeed(UserFeedEntry entry) {
        userFeed.add(entry);
    }

}
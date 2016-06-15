package de.hohenheim.controller;

import de.hohenheim.form.LoginForm;
import de.hohenheim.form.RegisterForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bunyaminbilgin on 08.06.16.
 */
//Diese Klasse ist für die Einloggprozesse zuständig.
@Controller
//Unter diesen URL kann man die Page erreichen
@RequestMapping("/")
public class UserController {
    //Wenn ich auf URL/login gehe:
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    //Hier kommt alles rein, was der Seite als Model (Formulare, Date, Übersetzungen... ) mitgeben soll
    public String getLoginPage(ModelMap modelMap) {
        modelMap.addAttribute("login", new LoginForm());
        //Diese Seite soll im Template "login" heissen.
        return "login";
    }
        @RequestMapping(value = "/login", method = RequestMethod.POST)
        public String postLoginPage(@ModelAttribute(value = "login") LoginForm loginForm,  HttpServletRequest request){
            //Soll nach Startseite redirektieren.
            return "redirect:/";

    }
    @RequestMapping(value = "register", method = RequestMethod.GET)
     public String getRegisterPage (ModelMap modelMap){
        modelMap.addAttribute("register", new RegisterForm());
        return "register";
    }

}

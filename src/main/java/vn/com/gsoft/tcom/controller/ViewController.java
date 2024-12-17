package vn.com.gsoft.tcom.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.com.gsoft.tcom.service.FolderService;

@Slf4j
@Controller
@RequestMapping("/folders")
public class ViewController {
    @Autowired
    private FolderService service;

    @GetMapping("/tree")
    public String listItems(Model model) throws Exception {
        return "tree";
    }
}
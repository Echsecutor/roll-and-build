package de.echsecutables.rollandbuild;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@ApiResponses(value = {
        @ApiResponse(code = 400, message = "nope"),
        @ApiResponse(code = 500, message = "BAM!")
})
public class SpringSessionJdbcController {


    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        List<String> favoriteColors = getFavColors(session);
        model.addAttribute("favoriteColors", favoriteColors);
        model.addAttribute("sessionId", session.getId());
        return model.toString();
    }

    @ApiOperation(value = "Add color to session")
    @PostMapping("/saveColor")
    public String saveMessage
            (@RequestParam("color") String color,
             HttpServletRequest request) {

        List<String> favoriteColors
                = getFavColors(request.getSession());
        if (!StringUtils.isEmpty(color)) {
            favoriteColors.add(color);
            request.getSession().
                    setAttribute("favoriteColors", favoriteColors);
        }
        return "redirect:/";
    }

    private List<String> getFavColors(HttpSession session) {
        List<String> favoriteColors = (List<String>) session
                .getAttribute("favoriteColors");

        if (favoriteColors == null) {
            favoriteColors = new ArrayList<>();
        }
        return favoriteColors;
    }
}
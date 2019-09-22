package com.shop.glass.shop.web;

import com.shop.glass.shop.domain.GlassShopUser;
import com.shop.glass.shop.service.GlassShopUserService;
import com.shop.glass.shop.util.ResponseUtil;
import com.shop.glass.shop.util.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.shop.glass.shop.util.code.ResponseCode.USER_INVALID_PASSWORD;
import static com.shop.glass.shop.util.code.ResponseCode.USER_MOBILE_EXIST;

@RestController
@RequestMapping("/user")
@Validated
public class GlassShopUserController {

    @Autowired
    private GlassShopUserService glassShopUserService;

    @GetMapping("/list")
    public Object list(String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit){
        List<GlassShopUser> userList = glassShopUserService.queryUser(name, page, limit);
        return ResponseUtil.okList(userList);
    }


    private Object validate(GlassShopUser gsuser) {
        String name = gsuser.getUsername();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }
        String password = gsuser.getPassword();
        if (StringUtils.isEmpty(password) || password.length() < 6) {
            return ResponseUtil.fail(USER_INVALID_PASSWORD, "用户密码长度不能小于6");
        }
        Byte gender = gsuser.getGender();
        if (StringUtils.isEmpty(gender)) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    @PostMapping("/create")
    public Object create(@RequestBody GlassShopUser gsUser) {
        Object error = validate(gsUser);
        if (error != null) {
            return error;
        }
        //判断用户是否已经存在
        String username = gsUser.getUsername();
        List<GlassShopUser> adminList = glassShopUserService.findUser(username);
        if (adminList.size() > 0) {
            return ResponseUtil.fail(USER_MOBILE_EXIST, "用户已经存在");
        }

        String rawPassword = gsUser.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        //对密码进行加密
        String encodedPassword = encoder.encode(rawPassword);
        gsUser.setPassword(encodedPassword);
        //真正的添加操作在这里
        glassShopUserService.add(gsUser);
        return ResponseUtil.ok(gsUser);
    }

    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        GlassShopUser gsUser = glassShopUserService.findById(id);
        return ResponseUtil.ok(gsUser);
    }

    @PostMapping("/update")
    public Object update(@RequestBody GlassShopUser gsUser) {
        Object error = validate(gsUser);
        if (error != null) {
            return error;
        }

        gsUser.setPassword(null);

        Integer anotherUserId = gsUser.getId();
        if (anotherUserId == null) {
            return ResponseUtil.badArgument();
        }

        if (glassShopUserService.updateById(gsUser) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(gsUser);
    }

}

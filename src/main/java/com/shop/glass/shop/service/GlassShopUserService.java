package com.shop.glass.shop.service;

import com.github.pagehelper.PageHelper;
import com.shop.glass.shop.dao.GlassShopUserMapper;
import com.shop.glass.shop.domain.GlassShopUser;
import com.shop.glass.shop.domain.GlassShopUserExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GlassShopUserService {
    private final GlassShopUser.Column[] result = new GlassShopUser.Column[]{GlassShopUser.Column.id, GlassShopUser.Column.username};
    @Resource
    private GlassShopUserMapper glassShopUserMapper;

    public List<GlassShopUser> findUser(String username) {
        GlassShopUserExample example = new GlassShopUserExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return glassShopUserMapper.selectByExample(example);
    }

    public GlassShopUser findAdmin(Integer id) {
        return glassShopUserMapper.selectByPrimaryKey(id);
    }

    public List<GlassShopUser> queryUser(String name, Integer page, Integer limit) {
        GlassShopUserExample example = new GlassShopUserExample();
        GlassShopUserExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andUsernameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, limit);
        return glassShopUserMapper.selectByExampleSelective(example, result);
    }


    public void add(GlassShopUser gsUser) {
        gsUser.setAddTime(LocalDateTime.now());
        gsUser.setUpdateTime(LocalDateTime.now());
        glassShopUserMapper.insertSelective(gsUser);
    }

    public GlassShopUser findById(Integer id) {
        return glassShopUserMapper.selectByPrimaryKeySelective(id, result);
    }

    public int updateById(GlassShopUser gsUser) {
        gsUser.setUpdateTime(LocalDateTime.now());
        return glassShopUserMapper.updateByPrimaryKeySelective(gsUser);
    }
}

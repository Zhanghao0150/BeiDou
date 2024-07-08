package org.gms.service;

import java.util.List;
import java.util.stream.Collectors;

import org.gms.client.Character;
import org.gms.dao.entity.AccountsDO;
import org.gms.dao.entity.CharactersDO;
import org.gms.dao.mapper.AccountsMapper;
import org.gms.dao.mapper.CharactersMapper;
import org.gms.net.server.Server;
import org.gms.net.server.world.World;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

@Service
public class PlayerService {
    private final AccountsMapper accountsMapper;
    private final CharactersMapper charactersMapper;

    @Autowired
    public PlayerService(AccountsMapper accountsMapper, CharactersMapper charactersMapper) {
        this.charactersMapper = charactersMapper;
        this.accountsMapper = accountsMapper;
    }

    public class PlayserDO {
        public String id, name, job, level, gm;
        @JsonProperty("mapid")
        public int map;
    }

    public Page<PlayserDO> getPlayerList(Integer page, Integer size, Integer id, String name, String map) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (id != null)
            queryWrapper.eq("id", id);
        if (map != null)
            queryWrapper.eq("map", id);
        if (name != null)
            queryWrapper.like("name", name);
        if (page == null)
            page = 1;
        if (size == null)
            size = Integer.MAX_VALUE;
        // 原始数据
        Page<CharactersDO> result = charactersMapper.paginateWithRelations(page, size, queryWrapper);
        // 转换数据
        Page<PlayserDO> pageResult = new Page<>(page, size);
        pageResult.setTotalRow(result.getTotalRow());
        pageResult.setRecords(result.getRecords().stream().map(e -> {
            PlayserDO playserDO = new PlayserDO();
            playserDO.id = e.getId().toString();
            playserDO.name = e.getName();
            playserDO.level = e.getLevel().toString();
            playserDO.job = e.getJob().toString();
            playserDO.map = e.getMap();
            playserDO.gm = e.getGm().toString();
            return playserDO;
        }).collect(Collectors.toList()));
        pageResult.setPageNumber(result.getPageNumber());
        return pageResult;
    }

    public class UpdatePlayerDTO {
        public Integer id;
        public Integer type;
        public String player;
        public String playerId;
        public Integer quantity;

    }

    public void updatePlayer(UpdatePlayerDTO submitUpdatePlayer) {
        CharactersDO charactersDO = charactersMapper.selectOneById(Integer.parseInt(submitUpdatePlayer.playerId));
        AccountsDO accountDO = accountsMapper.selectOneById(charactersDO.getAccountid());
        Character onLinePlayer = getOnlinePlayer(charactersDO.getId());
        if (onLinePlayer != null) {
            // TODO 在线玩家发奖励的逻辑是另外一套
        } else {
            switch (submitUpdatePlayer.type) {
                case 0:
                    accountDO.setNxCredit(accountDO.getNxCredit() + submitUpdatePlayer.quantity);
                    break;
                case 1:
                    accountDO.setMaplePoint(accountDO.getMaplePoint() + submitUpdatePlayer.quantity);
                    break;
                case 2:
                    accountDO.setNxPrepaid(accountDO.getNxPrepaid() + submitUpdatePlayer.quantity);
                    break;
                case 3:
                    charactersDO.setMeso(charactersDO.getMeso() + submitUpdatePlayer.quantity);
                    break;
                case 4:
                    charactersDO.setExp(charactersDO.getExp() + submitUpdatePlayer.quantity);
                    break;
                case 5:
                    // TODO 发道具待实现
                    break;
                default:
                    break;
            }
            charactersMapper.update(charactersDO);
            accountsMapper.update(accountDO);
        }

    }

    // 判断玩家是否在线
    public org.gms.client.Character getOnlinePlayer(Integer id) {
        List<World> words = Server.getInstance().getWorlds();
        for (World world : words) {
            for (org.gms.client.Character character : world.getPlayerStorage().getAllCharacters()) {
                if (character.getId() == id) {
                    return character;
                }
            }
        }
        return null;
    }

}

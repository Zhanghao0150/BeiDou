package org.gms.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("players")
public class PlayerDTO implements Serializable {
    private Integer playerId;
    private String player;
    private Integer type;
    private Integer id;
    private Integer quantity;
    private Integer rate;
    private Integer str;
    private Integer dex;
    @JsonProperty("int")
    private Integer _int;
    private Integer luk;
    private Integer hp;
    private Integer mp;
    private Integer pAtk;
    private Integer mAtk;
    private Integer pDef;
    private Integer mDef;
    private Integer acc;
    private Integer avoid;
    private Integer speed;
    private Integer jump;
    private Integer expire;

}

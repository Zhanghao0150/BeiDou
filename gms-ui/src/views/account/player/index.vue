<template>
  <div class="container" :loading="true">
    <Breadcrumb />
    <a-card class="general-card" :title="$t('menu.account.player')">
      <a-row style="margin-bottom: 16px">
        <a-col :flex="1">
          <a-form
            :model="filterForm"
            :label-col-props="{ span: 8 }"
            :wrapper-col-props="{ span: 16 }"
          >
            <a-row :gutter="16">
              <a-col :span="6">
                <a-form-item :label="$t('account.player.id')">
                  <a-input-number v-model="filterForm.id" />
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item :label="$t('account.player.name')">
                  <a-input v-model="filterForm.name" />
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item :label="$t('account.player.mapId')">
                  <a-input-number v-model="filterForm.map" />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </a-col>
        <a-divider style="height: 84px" direction="vertical" />
        <a-col :flex="'86px'" style="text-align: right">
          <a-space direction="vertical" :size="18">
            <a-button type="primary" @click="loadData()">
              <template #icon>
                <icon-search />
              </template>
              {{ $t('button.load') }}
            </a-button>
            <a-button @click="resetClick">
              <template #icon>
                <icon-refresh />
              </template>
              {{ $t('button.reset') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>
      <a-divider />
      <a-row style="margin-bottom: 16px">
        <a-col>
          <a-space>
            <a-button type="primary" @click="refreshClick">
              <template #icon>
                <icon-plus />
              </template>
              {{ $t('button.refresh') }}
            </a-button>
            <a-button type="primary" @click="globalGiveClick">
              <template #icon>
                <icon-plus />
              </template>
              {{ $t('account.player.button.globalGive') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>
      <a-table
        row-key="id"
        :loading="loading"
        :data="tableData"
        column-resizable
        :pagination="false"
        :bordered="{ cell: true }"
        :scroll="{ y: 'calc(100vh - 502px)' }"
      >
        <template #columns>
          <a-table-column
            :title="$t('account.player.id')"
            data-index="id"
            :width="100"
            align="center"
          />
          <a-table-column
            :title="$t('account.player.name')"
            data-index="name"
            :width="200"
            align="center"
          />
          <a-table-column
            :title="$t('account.player.map')"
            data-index="mapid"
            :width="200"
            align="center"
          />
          <a-table-column
            :title="$t('account.player.job')"
            data-index="job"
            :width="200"
            align="center"
          />
          <a-table-column
            :title="$t('account.player.level')"
            data-index="level"
            :width="200"
            align="center"
          />
          <a-table-column
            :title="$t('account.player.gm.level')"
            data-index="gm"
            :width="200"
            align="center"
          />
          <a-table-column :title="$t('account.list.column.operate')">
            <template #cell="{ record }">
              <a-button type="text" size="mini" @click="giveClick(record)">
                {{ $t('account.player.button.give') }}
              </a-button>
            </template>
          </a-table-column>
        </template>
      </a-table>
      <a-pagination
        style="margin-top: 20px"
        :total="total"
        :page-size="size"
        :current="page"
        show-total
        show-jumper
        show-page-size
        :page-size-options="[7, 14, 35, 70]"
        @change="pageChange"
        @page-size-change="pageSizeChange"
      />
    </a-card>

    <a-modal
      v-model:visible="giveFormVisible"
      :title="giveFormTitle"
      :ok-loading="loading"
      :mask-closable="false"
      :esc-to-close="false"
      :ok-text="$t('account.player.give')"
      :on-before-ok="submitClick"
    >
      <a-form :model="formData">
        <a-form-item
          v-if="formData.playerId !== 0"
          :label="$t('account.player.form.player')"
        >
          <a-space>
            <a-tag color="red">{{ formData.playerId }}</a-tag>
            <a-tag color="blue">{{ formData.player }}</a-tag>
          </a-space>
        </a-form-item>
        <a-form-item :label="$t('account.player.form.type')">
          <a-select
            v-model="formData.type"
            :options="typeOptions"
            :field-names="typeFieldNames"
          />
        </a-form-item>
        <a-form-item
          v-if="formData.type === 5 || formData.type === 6"
          :label="$t('account.player.form.id')"
        >
          <a-input-number v-model="formData.id" />
        </a-form-item>
        <a-form-item
          v-if="formData.type < 6"
          :label="$t('account.player.form.quantity')"
        >
          <a-input-number v-model="formData.quantity" />
        </a-form-item>
        <a-form-item
          v-if="formData.type > 6 && formData.type < 11"
          :label="$t('account.player.form.rate')"
        >
          <a-input-number v-model="formData.rate" />
        </a-form-item>
        <a-form-item
          v-if="formData.type === 6"
          :label="$t('account.player.form.str')"
        >
          <a-input-number v-model="formData.str" />
        </a-form-item>
        <a-form-item
          v-if="formData.type === 6"
          :label="$t('account.player.form.dex')"
        >
          <a-input-number v-model="formData.dex" />
        </a-form-item>
        <a-form-item
          v-if="formData.type === 6"
          :label="$t('account.player.form.int')"
        >
          <a-input-number v-model="formData.int" />
        </a-form-item>
        <a-form-item
          v-if="formData.type === 6"
          :label="$t('account.player.form.luk')"
        >
          <a-input-number v-model="formData.luk" />
        </a-form-item>
        <a-form-item
          v-if="formData.type === 6"
          :label="$t('account.player.form.hp')"
        >
          <a-input-number v-model="formData.hp" />
        </a-form-item>
        <a-form-item
          v-if="formData.type === 6"
          :label="$t('account.player.form.mp')"
        >
          <a-input-number v-model="formData.mp" />
        </a-form-item>
        <a-form-item
          v-if="formData.type === 6"
          :label="$t('account.player.form.pAtk')"
        >
          <a-input-number v-model="formData.pAtk" />
        </a-form-item>
        <a-form-item
          v-if="formData.type === 6"
          :label="$t('account.player.form.mAtk')"
        >
          <a-input-number v-model="formData.mAtk" />
        </a-form-item>
        <a-form-item
          v-if="formData.type === 6"
          :label="$t('account.player.form.pDef')"
        >
          <a-input-number v-model="formData.pDef" />
        </a-form-item>
        <a-form-item
          v-if="formData.type === 6"
          :label="$t('account.player.form.mDef')"
        >
          <a-input-number v-model="formData.mDef" />
        </a-form-item>
        <a-form-item
          v-if="formData.type === 6"
          :label="$t('account.player.form.acc')"
        >
          <a-input-number v-model="formData.acc" />
        </a-form-item>
        <a-form-item
          v-if="formData.type === 6"
          :label="$t('account.player.form.avoid')"
        >
          <a-input-number v-model="formData.avoid" />
        </a-form-item>
        <a-form-item
          v-if="formData.type === 6"
          :label="$t('account.player.form.speed')"
        >
          <a-input-number v-model="formData.speed" />
        </a-form-item>
        <a-form-item
          v-if="formData.type === 6"
          :label="$t('account.player.form.jump')"
        >
          <a-input-number v-model="formData.jump" />
        </a-form-item>
        <a-form-item
          v-if="formData.type === 6"
          :label="$t('account.player.form.expire')"
        >
          <a-input-number
            v-model="formData.expire"
            :placeholder="$t('account.player.form.expire.placeholder')"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import useLoading from '@/hooks/loading';
  import { ref } from 'vue';
  import { AccountState } from '@/store/modules/account/types';
  import { Message } from '@arco-design/web-vue';
  import { useI18n } from 'vue-i18n';
  import { getPlayerList, GiveForm, givePlayerSrc } from '@/api/player';

  const { t } = useI18n();
  const { loading, setLoading } = useLoading(false);
  const tableData = ref<AccountState[]>([]);
  const total = ref(0);
  const page = ref(1);
  const size = ref(14);
  const filterForm = ref<{
    id?: number;
    name?: string;
    map?: number;
  }>({
    id: undefined,
    name: undefined,
    map: undefined,
  });
  const giveFormVisible = ref(false);
  const giveFormTitle = ref('');
  const formData = ref<GiveForm>({
    playerId: undefined,
    player: undefined,
    type: 0,
    id: undefined,
    quantity: undefined,
    rate: undefined,
    str: undefined,
    dex: undefined,
    int: undefined,
    luk: undefined,
    hp: undefined,
    mp: undefined,
    pAtk: undefined,
    mAtk: undefined,
    pDef: undefined,
    mDef: undefined,
    acc: undefined,
    avoid: undefined,
    speed: undefined,
    jump: undefined,
    expire: undefined,
  });

  const typeFieldNames = { value: 'value', label: 'label' };
  const typeOptions = ref([{ value: 0, label: t('account.player.nxCredit') }]);

  const loadData = async () => {
    setLoading(true);
    try {
      const { data } = await getPlayerList(
        page.value,
        size.value,
        filterForm.value.id,
        filterForm.value.name,
        filterForm.value.map
      );
      tableData.value = data.records;
      total.value = data.totalRow;
    } finally {
      setLoading(false);
    }
  };
  loadData();

  const refreshClick = () => {
    page.value = 1;
    loadData();
  };

  const pageChange = (data: number) => {
    page.value = data;
    loadData();
  };

  const pageSizeChange = (data: number) => {
    page.value = 1;
    size.value = data;
    loadData();
  };

  const resetClick = () => {
    filterForm.value.id = undefined;
    filterForm.value.name = undefined;
    filterForm.value.map = undefined;
    page.value = 1;
    loadData();
  };

  const globalGiveClick = () => {
    giveFormTitle.value = '全服发放资源';
    typeOptions.value = [
      { value: 0, label: t('account.player.nxCredit') },
      { value: 1, label: t('account.player.nxPrepaid') },
      { value: 2, label: t('account.player.maplePoint') },
      { value: 3, label: t('account.player.mesos') },
      { value: 4, label: t('account.player.exp') },
      { value: 5, label: t('account.player.item') },
      { value: 6, label: t('account.player.equip') },
    ];
    formData.value.playerId = 0;
    formData.value.player = undefined;
    giveFormVisible.value = true;
  };

  const giveClick = (data: any) => {
    giveFormTitle.value = '发放资源';
    typeOptions.value = [
      { value: 0, label: t('account.player.nxCredit') },
      { value: 1, label: t('account.player.nxPrepaid') },
      { value: 2, label: t('account.player.maplePoint') },
      { value: 3, label: t('account.player.mesos') },
      { value: 4, label: t('account.player.exp') },
      { value: 5, label: t('account.player.item') },
      { value: 6, label: t('account.player.equip') },
      { value: 7, label: t('account.player.expRate') },
      { value: 8, label: t('account.player.mesosRate') },
      { value: 9, label: t('account.player.dropRate') },
      { value: 10, label: t('account.player.bossRate') },
    ];
    formData.value.playerId = data.id;
    formData.value.player = data.name;
    giveFormVisible.value = true;
  };

  const submitClick = async () => {
    setLoading(true);
    try {
      await givePlayerSrc(formData.value);
      Message.success(t('message.success'));
    } finally {
      setLoading(false);
    }
  };
</script>

<script lang="ts">
  export default {
    name: 'Player',
  };
</script>

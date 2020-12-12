# tater

## 使い方

#### ローカル環境

* 初回は以下のように local.properties のリンクを所定の場所に作成する

```bash
cd /etc
sudo mkdir tater & cd tater
sudo ln -s <app directory>/environment/tater-api/local.properties app.properties
```

* IntelliJなどでアプリを実行する (DB, wiremockはE2E環境を参照)

#### E2E環境

* 初回は以下のように Namespace と Secret を作成する

```bash
cd <app directory>/environment & kubectl apply -f namespace-e2e.yml
cd <app directory>/environment/tater-api & ./create-secret.sh e2e.properties
```

* 必要に応じて Docker Image をビルドする

```bash
cd <app directory>/environment/tater-api & ./build-image.sh
cd <app directory>/environment/tater-db & ./build-image.sh
```

* `environment/prep-e2e.sh` でE2E用の環境を全て構築
* `environment/clean-after-e2e.sh` でE2E用の環境を全て削除

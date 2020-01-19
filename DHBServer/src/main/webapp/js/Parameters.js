/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

SERVER_HOST = "127.0.0.1";
SERVER_PORT = "8080";

WS_RESOURCE = "DHBServer";
WS_FULL_PATH = "ws://" + SERVER_HOST + ":" + SERVER_PORT + "/" + WS_RESOURCE + "/";

WS_ATTACK_STATUS_ENDPOINT = WS_FULL_PATH + "attack";
WS_BUCKET_STATUS_ENDPOINT = WS_FULL_PATH + "bucket";
WS_PLAN_ATTACK_ENDPOINT   = WS_FULL_PATH + "plan";
WS_REVOKE_BUCKET_ENDPOINT = WS_FULL_PATH + "revoke";


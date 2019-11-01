/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

SERVER_HOST = "localhost";
SERVER_PORT = "8080";

WS_RESOURCE = "DHBServer";
WS_FULL_PATH = "ws://" + SERVER_HOST + ":" + SERVER_PORT + "/" + WS_RESOURCE + "/";

WS_INITIAL_STATUS_ENDPOINT = WS_FULL_PATH + "test_initial_config";
WS_BUCKET_STATUS_ENDPOINT  = WS_FULL_PATH + "bucket";


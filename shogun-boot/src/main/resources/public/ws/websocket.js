/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2020-present terrestris GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
const wsUrl = '/websocket';
let ws;
let stompClient;

function connect() {
    ws = new SockJS(wsUrl);
    stompClient = Stomp.over(ws);
    stompClient.connect({}, function(frame) {
        setConnected(true);
        stompClient.subscribe('/topic/public', function(message) {
            showEvent(message);
        });

        stompClient.subscribe('/user/queue/reply', function(message) {
            showEvent(message);
        });
    }, function(message) {
        console.log('Error while establishing a connection to ' + wsUrl);
        setConnected(false);
    });
}

function disconnect() {
    if (ws !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
}

function sendMessage() {
    if (ws === null) {
        console.log('Socket not connected!');
    }

    let messageText = $('#message').val();
    let messageObject = {
        message: messageText
    }

    //stompClient.send('/app/hello', {}, JSON.stringify(messageObject));
    stompClient.send('/topic/public', {}, JSON.stringify(messageObject));

    $('#message').val('')
}

function showEvent(e) {
    if (e && e.body) {
        const message = JSON.parse(e.body);
        $('#messages').append('<tr><td>' + message.message + '</td></tr>');
    }
}

function setConnected(connected) {
    $('#connect').prop('disabled', connected);
    $('#disconnect').prop('disabled', !connected);
    $('#send-message').prop('disabled', !connected);
    $('#events').html('');

    if (connected) {
        $('#conversation').show();
    } else {
        $('#conversation').hide();
    }
}

$(function () {
    setConnected(false);

    $('form').on('submit', function(e) {
        e.preventDefault();
    });
    $('#connect').click(function() {
        connect();
    });
    $('#disconnect').click(function() {
        disconnect();
    });
});

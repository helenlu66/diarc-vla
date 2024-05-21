/**
 * @author Lucien Bao
 * @version 0.9
 * @date 20 May 2024
 * RobotChat. Defines a chat interface component for issuing commands and
 * receiving feedback through DIARC.
 */

// TODO
/*
- Add a sidebar to let the user choose which robots they are speaking to
    - Akin to choosing between message recipients/group chats
    - This will be used for the .addListener() call for the utterance builder
    + Switching between channels
    + Logging of message history
- Add a text box to let the user specify themselves
    - This will be used for the .setSpeaker() call for the utterance builder
*/

import React, { useState } from 'react';

import "@chatscope/chat-ui-kit-styles/dist/default/styles.min.css"
import {
    MainContainer,
    ChatContainer,
    MessageList,
    Message,
    MessageInput,
    Conversation,
    ConversationHeader,
    Sidebar,
    Avatar,
    ConversationList
} from "@chatscope/chat-ui-kit-react";

class Chat {
    robotName: string;
    robotInfo: string;
    profileImagePath: string;
    messageList: MessageProps[];
    focusThis: any;

    constructor(robotName: string, robotInfo: string, profileImagePath: string,
        messageList: MessageProps[], focusThis: any) {
        this.robotName = robotName;
        this.robotInfo = robotInfo;
        this.profileImagePath = profileImagePath;
        this.messageList = messageList;
        this.focusThis = focusThis;
    }

    avatar() {
        return (
            <Avatar
                name={this.robotName}
                src={this.profileImagePath}
            />
        );
    }

    conversation(index: number) {
        return (
            <Conversation
                key={index}
                info={this.messageList.length > 0 ?
                    this.messageList.slice(-1)[0].message
                    : "No messages yet"}
                lastSenderName={this.messageList.length > 0 ?
                    this.messageList.slice(-1)[0].sender
                    : ""}
                name={this.robotName}
                onClick={(e) => this.focusThis(this)}
            >
                {this.avatar()}
            </Conversation>
        );
    }

    conversationHeader() {
        return (
            <ConversationHeader>
                {this.avatar()}
                <ConversationHeader.Content
                    info={this.robotInfo}
                    userName={this.robotName}
                />
            </ConversationHeader>
        );
    }

    renderMessageList() {
        return this.messageList.map(
            (message, index) => <Message key={index} model={message} />)
    }
}

// Subset of 'model' prop at https://chatscope.io/storybook/react/?path=/docs/components-message--docs
type MessageProps = {
    message: string,
    sender: string,
    direction: "incoming" | "outgoing" | 0 | 1,
    position: "single" | "first" | "normal" | "last" | 0 | 1 | 2 | 3
}

const RobotChat: React.FC<{}> = () => {
    const [currentChat, setCurrentChat] =
        useState(new Chat("", "", "", [], null));

    const [dempsterChat, setDempsterChat] = useState(
        new Chat("dempster", "NAO robot", "/heroimage.svg",
            [{
                message: "hello dempster",
                sender: "evan",
                direction: "outgoing",
                position: "single"
            },
            {
                message: "hello evan",
                sender: "dempster",
                direction: "incoming",
                position: "single"
            },
            {
                message: "stand",
                sender: "evan",
                direction: "outgoing",
                position: "single"
            },
            {
                message: "okay",
                sender: "dempster",
                direction: "incoming",
                position: "first"
            },
            {
                message: "INFO goToPosture: ([agent:dempster]) Stand",
                sender: "dempster",
                direction: "incoming",
                position: "last"
            }],
            setCurrentChat)
    );

    const [shaferChat, setShaferChat] = useState(
        new Chat("shafer", "NAO robot", "/robot.png",
            [{
                message: "hello shafer",
                sender: "ravenna",
                direction: "outgoing",
                position: "single"
            },
            {
                message: "hello ravenna",
                sender: "shafer",
                direction: "incoming",
                position: "single"
            },
            {
                message: "please nod",
                sender: "ravenna",
                direction: "outgoing",
                position: "single"
            },
            {
                message: "okay",
                sender: "shafer",
                direction: "incoming",
                position: "first"
            },
            {
                message: "I can not nod because I do not know how to nod",
                sender: "shafer",
                direction: "incoming",
                position: "last"
            },
            {
                message: "i will teach you how to nod",
                sender: "ravenna",
                direction: "outgoing",
                position: "single"
            },
            {
                message: "I can not learn how to nod because learning how to nod is admin Goal",
                sender: "shafer",
                direction: "incoming",
                position: "single"
            }],
            setCurrentChat)
    );

    const [chats, setChat] = useState([dempsterChat, shaferChat]);

    const [conversations, setConversations] = useState(
        <ConversationList>
            {chats.map((chat, index) => chat.conversation(index))}
        </ConversationList>
    )

    return (
        <div className='size-9/12'>
            <MainContainer>
                <Sidebar position="left">
                    {conversations}
                </Sidebar>

                <ChatContainer>
                    {currentChat.conversationHeader()}

                    <MessageList>
                        {currentChat.renderMessageList()}
                    </MessageList>

                    <MessageInput
                        placeholder={"Talk with " + currentChat.robotName
                            + "..."}
                        attachButton={false}
                    />
                </ChatContainer>
            </MainContainer>
        </div>
    );
}

export default RobotChat;


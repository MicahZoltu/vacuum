package com.zoltu.Vacuum.Messaging;

public interface IListener
{
	public void ReceiveMessage(com.google.protobuf.Message pMessage);
}

import React, { useEffect, useCallback, useState, useRef } from 'react';
import useWebSocket from 'react-use-websocket';

const tasks = [
    "pick up the black bowl between the plate and the ramekin and place it on the plate",
    "pick up the black bowl next to the ramekin and place it on the plate",
    "pick up the black bowl from table center and place it on the plate",
    "pick up the black bowl on the cookie box and place it on the plate",
    "pick up the black bowl in the top drawer of the wooden cabinet and place it on the plate",
    "pick up the black bowl on the ramekin and place it on the plate",
    "pick up the black bowl next to the cookie box and place it on the plate",
    "pick up the black bowl on the stove and place it on the plate",
    "pick up the black bowl next to the plate and place it on the plate",
    "pick up the black bowl on the wooden cabinet and place it on the plate",
];


const VLAViewer = () => {
    const socketUrl = "ws://localhost:6006/ws";

    const [currentImage, setCurrentImage] = useState(null);
    const [status, setStatus] = useState('');
    const [error, setError] = useState('');
    const [isRunning, setIsRunning] = useState(false);
    const [fps, setFps] = useState(0);
    const [actionSubgoals, setActionSubgoals] = useState(null); // New state for action_subgoals

    const frameTimestamps = useRef([]);
    const calculateFPS = useCallback(() => {
        const now = performance.now();
        frameTimestamps.current.push(now);

        const oneSecondAgo = now - 1000;
        frameTimestamps.current = frameTimestamps.current.filter(t => t > oneSecondAgo);
        setFps(frameTimestamps.current.length);
    }, []);

    const { sendMessage, lastMessage, readyState } = useWebSocket(socketUrl, {
        onOpen: () => console.log("WebSocket connection opened"),
        onClose: () => {
            console.log("WebSocket connection closed");
            setIsRunning(false);
        },
        onError: (event) => console.error("WebSocket error:", event),
        shouldReconnect: () => true,
    });

    const sendTaskDescription = useCallback((taskDescription) => {
        console.log("Starting new task:", taskDescription);
        setCurrentImage(null);
        setStatus('Starting...');
        setError('');
        setIsRunning(true);
        setActionSubgoals(null); // Reset action_subgoals
        frameTimestamps.current = [];
        setFps(0);
        sendMessage(taskDescription);
    }, [sendMessage]);

    useEffect(() => {
        if (lastMessage !== null) {
            try {
                const data = JSON.parse(lastMessage.data);

                if (data.image) {
                    setCurrentImage(data.image);
                    calculateFPS();
                }

                if (data.status) {
                    setStatus(data.status);
                    if (
                        data.status.includes("success") ||
                        data.status.includes("max_steps_reached")
                    ) {
                        setIsRunning(false);
                    }
                }

                if (data.action_subgoals) {
                    setActionSubgoals(data.action_subgoals);
                }

                if (data.error) {
                    setError(data.error);
                    setIsRunning(false);
                }
            } catch (error) {
                console.error("Error parsing message:", error);
            }
        }
    }, [lastMessage, calculateFPS]);

    return (
        <div className="vla-container flex flex-col items-center gap-4 p-6">
            <div className="buttons flex flex-wrap gap-4">
                {tasks.map((task, index) => (
                    <button
                        key={index}
                        className="px-4 py-2 bg-gray-500 text-white font-semibold rounded-lg hover:bg-gray-600 transition duration-300 disabled:opacity-50"
                        onClick={() => sendTaskDescription(task)}
                        disabled={isRunning}
                    >
                        {task}
                    </button>
                ))}
            </div>

            <div className="status-container w-full max-w-lg text-center">
                {status && (
                    <p className="text-lg font-medium text-gray-700">
                        {status}
                    </p>
                )}
                {error && (
                    <p className="text-lg font-medium text-red-600">
                        {error}
                    </p>
                )}
                <p className="text-sm text-gray-500">
                    FPS: {fps}
                </p>
            </div>

            <div className="image-container w-full max-w-2xl aspect-video bg-gray-100 rounded-lg overflow-hidden relative">
                {currentImage ? (
                    <img
                        src={`data:image/jpeg;base64,${currentImage}`}
                        alt="Current state"
                        className="w-full h-full object-contain"
                        style={{ imageRendering: 'pixelated' }}
                    />
                ) : (
                    <div className="w-full h-full flex items-center justify-center text-gray-500">
                        Waiting for image...
                    </div>
                )}
            </div>

            {/* New Section for Action Subgoals */}
            {actionSubgoals && (
                <div className="action-subgoals-container w-full max-w-lg text-left">
                    <h3 className="text-lg font-semibold text-gray-700">Action Subgoals:</h3>
                    <ul className="list-disc list-inside">
                        {Object.entries(actionSubgoals).map(([key, value]) => (
                            <li key={key} className="text-gray-600">
                                <strong>{key}:</strong> {value}
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
};

export default VLAViewer;

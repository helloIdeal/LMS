// src/components/VideoBackground.js
import React from 'react';
import './VideoBackground.css';

const VideoBackground = () => {
  return (
    <>
      <div className="video-background">
        <video autoPlay muted loop playsInline className="video-bg">
          <source src="/library-bg.mp4" type="video/mp4" />
          Your browser does not support the video tag.
        </video>
      </div>
      <div className="overlay"></div>
    </>
  );
};

export default VideoBackground;